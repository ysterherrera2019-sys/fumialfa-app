import { useEffect, useState } from "react";
import api from "../api";
import TankDetailForm from "./TankDetailForm";
import PestDetailForm from "./PestDetailForm";

export default function OperatorDashboard({ onLogout }) {
  const [requests, setRequests] = useState([]);
  const [message, setMessage] = useState("");
  const [selectedCertificate, setSelectedCertificate] = useState(null);
  const [sentCertificateIds, setSentCertificateIds] = useState([]);

  const loadRequests = async () => {
    try {
      const res = await api.get("/api/service-requests");
      setRequests(res.data);
    } catch {
      setMessage("Error cargando solicitudes");
    }
  };

  useEffect(() => {
    loadRequests();
  }, []);

  const updateStatus = async (id, status) => {
    try {
      await api.put(`/api/service-requests/${id}/status`, { status });
      setMessage("Estado actualizado");
      loadRequests();
    } catch {
      setMessage("Error actualizando estado");
    }
  };

  const createCertificate = async (request) => {
    try {
      const res = await api.post(`/api/service-requests/${request.id}/certificate`);
      setMessage(`Certificado creado: ${res.data.certificateNumber}`);
      loadRequests();
    } catch (error) {
      setMessage(
        error.response?.data?.message ||
          error.response?.data ||
          "No se pudo crear el certificado"
      );
    }
  };

  const finalizeCertificate = async (certificateId) => {
    try {
      await api.put(`/api/certificates/${certificateId}/finalize`);
      setMessage("Certificado finalizado correctamente");
      loadRequests();
    } catch (error) {
      setMessage(
        error.response?.data?.message ||
          error.response?.data ||
          "Error finalizando certificado"
      );
    }
  };

  const openPdf = async (certificateId) => {
    try {
      const res = await api.get(`/api/certificates/${certificateId}/pdf`, {
        responseType: "blob"
      });

      const blob = new Blob([res.data], { type: "application/pdf" });
      const url = window.URL.createObjectURL(blob);

      const link = document.createElement("a");
      link.href = url;
      link.download = `certificado_${certificateId}.pdf`;
      document.body.appendChild(link);
      link.click();

      link.remove();
      window.URL.revokeObjectURL(url);
    } catch (error) {
      setMessage(
        error.response?.data?.message ||
          error.response?.data ||
          "No se pudo generar el PDF. Verifique que el certificado esté finalizado."
      );
    }
  };

  const sendCertificateEmail = async (certificateId) => {
    try {
      const res = await api.post(`/api/certificates/${certificateId}/send-email`);

      setMessage(
        typeof res.data === "string"
          ? res.data
          : "Certificado enviado al correo del cliente"
      );

      setSentCertificateIds((prev) => [...prev, certificateId]);
    } catch (error) {
      setMessage(
        error.response?.data?.message ||
          error.response?.data ||
          "Error enviando el certificado por correo"
      );
    }
  };

  const openTechnicalForm = (request) => {
    if (!request.certificateId) {
      setMessage("Primero debe crear el certificado");
      return;
    }

    setSelectedCertificate({
      id: request.certificateId,
      type: request.requestedServiceType
    });
  };

  const serviceLabel = (type) => {
    if (type === "TANK_CLEANING") return "Lavado de tanques";
    if (type === "PEST_CONTROL") return "Control de plagas";
    if (type === "EXTINGUISHERS") return "Extintores";
    return type;
  };

  const nextStatus = (status) => {
    if (status === "PENDING") return "CONTACTED";
    if (status === "CONTACTED") return "SCHEDULED";
    if (status === "SCHEDULED") return "COMPLETED";
    return null;
  };

  const nextLabel = (status) => {
    if (status === "PENDING") return "Contactar";
    if (status === "CONTACTED") return "Programar";
    if (status === "SCHEDULED") return "Completar";
    return null;
  };

  const canCreateCertificate = (request) => {
    return (
      request.status === "COMPLETED" &&
      !request.certificateId &&
      request.requestedServiceType !== "EXTINGUISHERS"
    );
  };

  const canOpenTechnicalForm = (request) => {
    return (
      request.certificateId &&
      (request.requestedServiceType === "TANK_CLEANING" ||
        request.requestedServiceType === "PEST_CONTROL")
    );
  };

  const canFinalizeCertificate = (request) => {
    return request.certificateId && request.requestedServiceType !== "EXTINGUISHERS";
  };

  const canViewPdf = (request) => {
    return request.certificateId && request.requestedServiceType !== "EXTINGUISHERS";
  };

  const canSendEmail = (request) => {
    return request.certificateId && request.requestedServiceType !== "EXTINGUISHERS";
  };

  const visibleRequests = requests.filter(
    (request) => !sentCertificateIds.includes(request.certificateId)
  );

  if (selectedCertificate?.type === "TANK_CLEANING") {
    return (
      <TankDetailForm
        certificateId={selectedCertificate.id}
        onBack={() => {
          setSelectedCertificate(null);
          loadRequests();
        }}
      />
    );
  }

  if (selectedCertificate?.type === "PEST_CONTROL") {
    return (
      <PestDetailForm
        certificateId={selectedCertificate.id}
        onBack={() => {
          setSelectedCertificate(null);
          loadRequests();
        }}
      />
    );
  }

  return (
    <div style={styles.page}>
      <div style={styles.container}>
        <div style={styles.header}>
          <h1 style={styles.title}>Bandeja del Operador</h1>
          <button style={styles.logoutButton} onClick={onLogout}>
            Cerrar sesión
          </button>
        </div>

        {message && <p style={styles.message}>{message}</p>}

        <table style={styles.table}>
          <thead>
            <tr>
              <th style={styles.th}>ID</th>
              <th style={styles.th}>Cliente</th>
              <th style={styles.th}>NIT / CC</th>
              <th style={styles.th}>Teléfono</th>
              <th style={styles.th}>Servicio</th>
              <th style={styles.th}>Estado</th>
              <th style={styles.th}>Certificado</th>
              <th style={styles.th}>Acciones</th>
            </tr>
          </thead>

          <tbody>
            {visibleRequests.map((request) => {
              const next = nextStatus(request.status);
              const label = nextLabel(request.status);

              return (
                <tr key={request.id}>
                  <td style={styles.td}>{request.id}</td>
                  <td style={styles.td}>{request.client?.businessName}</td>
                  <td style={styles.td}>{request.client?.taxId}</td>
                  <td style={styles.phoneTd}>{request.client?.phone}</td>
                  <td style={styles.td}>{serviceLabel(request.requestedServiceType)}</td>
                  <td style={styles.td}>{request.status}</td>
                  <td style={styles.td}>
                    {request.certificateId ? `#${request.certificateId}` : "Sin crear"}
                  </td>

                  <td style={styles.td}>
                    <div style={styles.actions}>
                      {next && (
                        <button style={styles.button} onClick={() => updateStatus(request.id, next)}>
                          {label}
                        </button>
                      )}

                      <button
                        style={{
                          ...styles.buttonCertificate,
                          opacity: canCreateCertificate(request) ? 1 : 0.4,
                          cursor: canCreateCertificate(request) ? "pointer" : "not-allowed"
                        }}
                        disabled={!canCreateCertificate(request)}
                        onClick={() => createCertificate(request)}
                      >
                        Crear certificado
                      </button>

                      <button
                        style={{
                          ...styles.buttonOpen,
                          opacity: canOpenTechnicalForm(request) ? 1 : 0.4,
                          cursor: canOpenTechnicalForm(request) ? "pointer" : "not-allowed"
                        }}
                        disabled={!canOpenTechnicalForm(request)}
                        onClick={() => openTechnicalForm(request)}
                      >
                        Abrir formulario
                      </button>

                      <button
                        style={{
                          ...styles.buttonFinalize,
                          opacity: canFinalizeCertificate(request) ? 1 : 0.4,
                          cursor: canFinalizeCertificate(request) ? "pointer" : "not-allowed"
                        }}
                        disabled={!canFinalizeCertificate(request)}
                        onClick={() => finalizeCertificate(request.certificateId)}
                      >
                        Finalizar
                      </button>

                      <button
                        style={{
                          ...styles.buttonPdf,
                          opacity: canViewPdf(request) ? 1 : 0.4,
                          cursor: canViewPdf(request) ? "pointer" : "not-allowed"
                        }}
                        disabled={!canViewPdf(request)}
                        onClick={() => openPdf(request.certificateId)}
                      >
                        Ver PDF
                      </button>

                      <button
                        style={{
                          ...styles.buttonSend,
                          opacity: canSendEmail(request) ? 1 : 0.4,
                          cursor: canSendEmail(request) ? "pointer" : "not-allowed"
                        }}
                        disabled={!canSendEmail(request)}
                        onClick={() => sendCertificateEmail(request.certificateId)}
                      >
                        Enviar
                      </button>
                    </div>
                  </td>
                </tr>
              );
            })}
          </tbody>
        </table>

        {visibleRequests.length === 0 && (
          <p style={styles.empty}>No hay solicitudes registradas.</p>
        )}
      </div>
    </div>
  );
}

const styles = {
  page: { minHeight: "100vh", background: "#111827", color: "#ffffff", padding: "30px" },
  container: { maxWidth: "1300px", margin: "0 auto" },
  header: { position: "relative", marginBottom: "20px" },
  title: { textAlign: "center", fontSize: "64px", margin: 0 },
  logoutButton: { position: "absolute", top: "10px", right: 0, padding: "10px 14px", border: "none", borderRadius: "6px", background: "#dc2626", color: "#ffffff", fontWeight: "bold", cursor: "pointer" },
  message: { textAlign: "center", color: "#d1d5db", fontSize: "20px" },
  table: { width: "100%", borderCollapse: "collapse", background: "#1f2937", borderRadius: "10px", overflow: "hidden" },
  th: { borderBottom: "1px solid #374151", padding: "14px", textAlign: "left", background: "#374151", fontSize: "20px" },
  td: { borderBottom: "1px solid #374151", padding: "14px", fontSize: "18px" },
  phoneTd: { borderBottom: "1px solid #374151", padding: "14px", fontSize: "22px", fontWeight: "bold", color: "#93c5fd" },
  actions: { display: "flex", gap: "8px", flexWrap: "wrap" },
  button: { padding: "8px 10px", border: "none", borderRadius: "6px", background: "#6b7280", color: "#ffffff", cursor: "pointer" },
  buttonCertificate: { padding: "8px 10px", border: "none", borderRadius: "6px", background: "#16a34a", color: "#ffffff" },
  buttonOpen: { padding: "8px 10px", border: "none", borderRadius: "6px", background: "#f59e0b", color: "#ffffff" },
  buttonFinalize: { padding: "8px 10px", border: "none", borderRadius: "6px", background: "#7c3aed", color: "#ffffff" },
  buttonPdf: { padding: "8px 10px", border: "none", borderRadius: "6px", background: "#0ea5e9", color: "#ffffff" },
  buttonSend: { padding: "8px 10px", border: "none", borderRadius: "6px", background: "#22c55e", color: "#ffffff" },
  empty: { marginTop: "20px", textAlign: "center", color: "#d1d5db" }
};