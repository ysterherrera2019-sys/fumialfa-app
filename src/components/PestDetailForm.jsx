import { useState } from "react";
import api from "../api";

export default function PestDetailForm({ certificateId, onBack }) {
  const [form, setForm] = useState({
    establishmentType: "",
    pestType: "",
    treatmentType: "",
    chemicalProduct: "",
    appliedDose: "",
    treatedAreas: "",
    observations: ""
  });

  const [message, setMessage] = useState("");
  const [saving, setSaving] = useState(false);

  const validate = () => {
    if (!form.establishmentType.trim()) return "Tipo de establecimiento obligatorio";
    if (!form.pestType.trim()) return "Tipo de plaga obligatorio";
    if (!form.treatmentType.trim()) return "Tipo de tratamiento obligatorio";
    if (!form.chemicalProduct.trim()) return "Producto químico obligatorio";
    if (!form.appliedDose.trim()) return "Dosis aplicada obligatoria";
    if (!form.treatedAreas.trim()) return "Áreas tratadas obligatorias";
    return null;
  };

  const handleChange = (e) => {
    setForm({ ...form, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    const error = validate();
    if (error) {
      setMessage(error);
      return;
    }

    setSaving(true);
    setMessage("");

    try {
      await api.put(`/api/certificates/${certificateId}/pest-detail`, form);
      setMessage("Datos de plagas guardados correctamente");
    } catch (error) {
      setMessage(error.response?.data?.message || error.response?.data || "Error guardando datos");
    } finally {
      setSaving(false);
    }
  };

  return (
    <div style={styles.page}>
      <div style={styles.card}>
        <h2 style={styles.title}>Detalle Técnico - Control de Plagas</h2>

        <form onSubmit={handleSubmit} style={styles.form}>
          <input style={styles.input} name="establishmentType" placeholder="Tipo establecimiento" value={form.establishmentType} onChange={handleChange} />
          <input style={styles.input} name="pestType" placeholder="Tipo plaga" value={form.pestType} onChange={handleChange} />
          <input style={styles.input} name="treatmentType" placeholder="Tipo tratamiento" value={form.treatmentType} onChange={handleChange} />
          <input style={styles.input} name="chemicalProduct" placeholder="Producto químico" value={form.chemicalProduct} onChange={handleChange} />
          <input style={styles.input} name="appliedDose" placeholder="Dosis aplicada" value={form.appliedDose} onChange={handleChange} />
          <input style={styles.input} name="treatedAreas" placeholder="Áreas tratadas" value={form.treatedAreas} onChange={handleChange} />

          <textarea style={styles.textarea} name="observations" placeholder="Observaciones" value={form.observations} onChange={handleChange} />

          <button type="submit" style={styles.saveButton} disabled={saving}>
            {saving ? "Guardando..." : "Guardar"}
          </button>
        </form>

        {message && <p style={styles.message}>{message}</p>}

        <button style={styles.backButton} onClick={onBack}>Volver</button>
      </div>
    </div>
  );
}

const styles = {
  page: { minHeight: "100vh", background: "#111827", display: "flex", justifyContent: "center", alignItems: "center", padding: "20px" },
  card: { background: "#1f2937", padding: "28px", borderRadius: "12px", width: "760px", color: "#fff" },
  title: { textAlign: "center", marginBottom: "18px" },
  form: { display: "flex", flexDirection: "column", gap: "10px" },
  input: { padding: "10px", borderRadius: "6px", border: "1px solid #6b7280", background: "#374151", color: "#ffffff", fontSize: "16px" },
  textarea: { padding: "10px", borderRadius: "6px", border: "1px solid #6b7280", background: "#374151", color: "#ffffff", fontSize: "16px", minHeight: "70px" },
  saveButton: { padding: "10px", border: "none", borderRadius: "6px", background: "#2563eb", color: "#fff", fontWeight: "bold", cursor: "pointer" },
  backButton: { display: "block", margin: "12px auto 0", padding: "8px 14px", border: "none", borderRadius: "6px", background: "#6b7280", color: "#fff", cursor: "pointer" },
  message: { textAlign: "center", fontSize: "18px", color: "#d1d5db" }
};