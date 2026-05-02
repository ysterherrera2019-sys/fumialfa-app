import { useState } from "react";
import api from "../api"; // 🔥 IMPORTANTE

export default function ClientRegistrationForm() {
  const [taxId, setTaxId] = useState("");

  const [form, setForm] = useState({
    businessName: "",
    legalName: "",
    email: "",
    phone: "",
    address: "",
    city: ""
  });

  const [services, setServices] = useState([]);
  const [message, setMessage] = useState("");
  const [loading, setLoading] = useState(false);

  const resetForm = () => {
    setForm({
      businessName: "",
      legalName: "",
      email: "",
      phone: "",
      address: "",
      city: ""
    });
    setServices([]);
  };

  const toggleService = (service) => {
    setServices((prev) =>
      prev.includes(service)
        ? prev.filter((s) => s !== service)
        : [...prev, service]
    );
  };

  const validate = () => {
    if (!taxId.trim()) return "NIT o Cédula es obligatorio";
    if (!form.businessName.trim()) return "Nombre comercial obligatorio";
    if (!form.legalName.trim()) return "Razón social obligatoria";
    if (!form.email.trim() || !form.email.includes("@")) return "Correo inválido";
    if (!form.phone.trim() || form.phone.length < 10) return "Teléfono inválido";
    if (!form.address.trim()) return "Dirección obligatoria";
    if (!form.city.trim()) return "Ciudad obligatoria";
    if (services.length === 0) return "Debe seleccionar al menos un servicio";
    return null;
  };

  const handleSearch = async () => {
    if (!taxId.trim()) {
      setMessage("Ingrese NIT o Cédula para buscar");
      return;
    }

    setLoading(true);

    try {
      const res = await api.get(
        `/api/clients/public/by-tax-id/${taxId.trim()}`
      );

      setForm({
        businessName: res.data.businessName || "",
        legalName: res.data.legalName || "",
        email: res.data.email || "",
        phone: res.data.phone || "",
        address: res.data.address || "",
        city: res.data.city || ""
      });

      setMessage("Cliente encontrado. Verifique los datos y seleccione servicios.");
    } catch {
      resetForm();
      setMessage("Cliente no encontrado. Complete los datos y seleccione servicios.");
    }

    setLoading(false);
  };

  const handleChange = (e) => {
    setForm({
      ...form,
      [e.target.name]: e.target.value
    });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    const error = validate();

    if (error) {
      setMessage(error);
      return;
    }

    setLoading(true);

    try {
      await api.post("/api/clients/public/register", {
        taxId: taxId.trim(),
        businessName: form.businessName.trim(),
        legalName: form.legalName.trim(),
        email: form.email.trim(),
        phone: form.phone.trim(),
        address: form.address.trim(),
        city: form.city.trim(),
        requestedServiceTypes: services
      });

      setTaxId("");
      resetForm();

      setMessage(
        "Gracias por registrar su solicitud con FUMIALFA. Nuestro equipo se comunicará con usted para coordinar la prestación del servicio."
      );
    } catch (err) {
      console.error(err);
      setMessage("No fue posible enviar la solicitud. Intente nuevamente.");
    }

    setLoading(false);
  };

  return (
    <div style={styles.page}>
      <div style={styles.card}>
        <h1 style={styles.title}>Registro de Cliente</h1>

        <div style={styles.searchRow}>
          <input
            style={styles.input}
            placeholder="NIT o Cédula"
            value={taxId}
            onChange={(e) => setTaxId(e.target.value)}
          />

          <button
            type="button"
            style={styles.buttonSecondary}
            onClick={handleSearch}
            disabled={loading}
          >
            Buscar
          </button>
        </div>

        <form onSubmit={handleSubmit} style={styles.form}>
          <input style={styles.input} name="businessName" placeholder="Nombre Comercial" value={form.businessName} onChange={handleChange} />
          <input style={styles.input} name="legalName" placeholder="Razón Social / Nombre completo" value={form.legalName} onChange={handleChange} />
          <input style={styles.input} name="email" type="email" placeholder="Correo" value={form.email} onChange={handleChange} />
          <input style={styles.input} name="phone" placeholder="Teléfono" value={form.phone} onChange={handleChange} />
          <input style={styles.input} name="address" placeholder="Dirección" value={form.address} onChange={handleChange} />
          <input style={styles.input} name="city" placeholder="Ciudad" value={form.city} onChange={handleChange} />

          <div style={styles.servicesBox}>
            <p style={styles.servicesTitle}>Servicios de interés</p>

            <label style={styles.checkboxLabel}>
              <input type="checkbox" checked={services.includes("TANK_CLEANING")} onChange={() => toggleService("TANK_CLEANING")} />
              Lavado y desinfección de tanques
            </label>

            <label style={styles.checkboxLabel}>
              <input type="checkbox" checked={services.includes("PEST_CONTROL")} onChange={() => toggleService("PEST_CONTROL")} />
              Control de plagas / fumigación
            </label>

            <label style={styles.checkboxLabel}>
              <input type="checkbox" checked={services.includes("EXTINGUISHERS")} onChange={() => toggleService("EXTINGUISHERS")} />
              Extintores
            </label>
          </div>

          <button type="submit" style={styles.buttonPrimary} disabled={loading}>
            {loading ? "Procesando..." : "Enviar solicitud"}
          </button>
        </form>

        {message && <p style={styles.message}>{message}</p>}
      </div>
    </div>
  );
}

const styles = {
  page: {
    minHeight: "100vh",
    background: "#111827",
    display: "flex",
    justifyContent: "center",
    alignItems: "center",
    padding: "20px"
  },
  card: {
    width: "100%",
    maxWidth: "620px",
    background: "#1f2937",
    padding: "28px",
    borderRadius: "12px"
  },
  title: {
    color: "#ffffff",
    textAlign: "center",
    marginBottom: "24px"
  },
  searchRow: {
    display: "flex",
    gap: "10px",
    marginBottom: "18px"
  },
  form: {
    display: "grid",
    gridTemplateColumns: "1fr 1fr",
    gap: "12px"
  },
  input: {
    padding: "10px",
    borderRadius: "6px",
    border: "1px solid #6b7280",
    background: "#374151",
    color: "#ffffff",
    fontSize: "15px"
  },
  servicesBox: {
    gridColumn: "1 / -1",
    background: "#374151",
    border: "1px solid #6b7280",
    borderRadius: "8px",
    padding: "14px",
    color: "#ffffff"
  },
  servicesTitle: {
    margin: "0 0 10px 0",
    fontWeight: "bold"
  },
  checkboxLabel: {
    display: "block",
    marginBottom: "8px"
  },
  buttonPrimary: {
    gridColumn: "1 / -1",
    padding: "12px",
    border: "none",
    borderRadius: "6px",
    background: "#2563eb",
    color: "#ffffff",
    fontWeight: "bold",
    cursor: "pointer"
  },
  buttonSecondary: {
    padding: "10px 16px",
    border: "none",
    borderRadius: "6px",
    background: "#6b7280",
    color: "#ffffff",
    fontWeight: "bold",
    cursor: "pointer"
  },
  message: {
    marginTop: "18px",
    textAlign: "center",
    color: "#d1d5db",
    fontSize: "16px"
  }
};