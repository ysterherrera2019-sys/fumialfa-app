import { useState } from "react";
import api from "../api";

const establishmentTypeOptions = [
  "Residencial",
  "Comercial",
  "Industrial",
  "Institucional",
  "Propiedad horizontal",
  "Otro"
];

const tankTypeOptions = [
  "Elevado",
  "Subterráneo",
  "En piso"
];

const tankCapacityOptions = [
  "500 LTS",
  "1000 LTS",
  "2000 LTS",
  "3000 LTS",
  "5000 LTS",
  "10.000 LTS",
  "Otra"
];

const tankMaterialOptions = [
  "Polietileno",
  "Baldosa",
  "Concreto",
  "Membrana",
  "Impermeabilizado",
  "Otro material"
];

const chemicalProductOptions = [
  "Hipoclorito al 0.5%",
  "Desengrasante biodegradable"
];

const systemTypeOptions = [
  "Hidrolavado",
  "Manual",
  "Cepillado"
];

const toolUsedOptions = [
  "Hidrolavadora",
  "Cepillo",
  "Escoba",
  "Trapero",
  "IPP"
];

export default function TankDetailForm({ certificateId, onBack }) {
  const [form, setForm] = useState({
    establishmentType: "",
    tankType: "",
    tankCapacity: "",
    otherTankCapacity: "",
    tankQuantity: "",
    tankMeasures: "",
    tankMaterial: "",
    chemicalProduct: "",
    systemType: "",
    toolUsed: "",
    observations: ""
  });

  const [message, setMessage] = useState("");
  const [saving, setSaving] = useState(false);

  const validate = () => {
    if (!form.establishmentType.trim()) {
      return "Tipo de establecimiento obligatorio";
    }

    if (!form.tankType.trim()) {
      return "Tipo de tanque obligatorio";
    }

    if (!form.tankCapacity.trim()) {
      return "Capacidad obligatoria";
    }

    if (
      form.tankCapacity === "Otra" &&
      !form.otherTankCapacity.trim()
    ) {
      return "Digite la capacidad";
    }

    if (!form.tankQuantity.trim()) {
      return "Cantidad obligatoria";
    }

    if (!form.tankMaterial.trim()) {
      return "Material obligatorio";
    }

    if (!form.chemicalProduct.trim()) {
      return "Producto químico obligatorio";
    }

    if (!form.systemType.trim()) {
      return "Sistema obligatorio";
    }

    if (!form.toolUsed.trim()) {
      return "Herramienta obligatoria";
    }

    return null;
  };

  const handleChange = (e) => {
    const { name, value } = e.target;

    setForm((prev) => ({
      ...prev,
      [name]: value,
      ...(name === "tankCapacity" && value !== "Otra"
        ? { otherTankCapacity: "" }
        : {})
    }));
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

    const payload = {
      ...form,
      tankCapacity:
        form.tankCapacity === "Otra"
          ? form.otherTankCapacity
          : form.tankCapacity
    };

    delete payload.otherTankCapacity;

    try {
      await api.put(
        `/api/certificates/${certificateId}/tank-detail`,
        payload
      );

      setMessage("Datos técnicos guardados correctamente");
    } catch (error) {
      setMessage(
        error.response?.data?.message ||
        error.response?.data ||
        "Error guardando datos"
      );
    } finally {
      setSaving(false);
    }
  };

  const renderSelect = (name, label, options) => (
    <select
      style={styles.input}
      name={name}
      value={form[name]}
      onChange={handleChange}
    >
      <option value="">{label}</option>

      {options.map((option) => (
        <option key={option} value={option}>
          {option}
        </option>
      ))}
    </select>
  );

  return (
    <div style={styles.page}>
      <div style={styles.card}>
        <h2 style={styles.title}>
          Detalle Técnico - Tanques
        </h2>

        <form onSubmit={handleSubmit} style={styles.form}>

          {renderSelect(
            "establishmentType",
            "Seleccione tipo de establecimiento",
            establishmentTypeOptions
          )}

          {renderSelect(
            "tankType",
            "Seleccione tipo de tanque",
            tankTypeOptions
          )}

          {renderSelect(
            "tankCapacity",
            "Seleccione capacidad",
            tankCapacityOptions
          )}

          {form.tankCapacity === "Otra" && (
            <input
              style={styles.input}
              name="otherTankCapacity"
              placeholder="Digite la capacidad"
              value={form.otherTankCapacity}
              onChange={handleChange}
            />
          )}

          <input
            style={styles.input}
            name="tankQuantity"
            placeholder="Cantidad"
            value={form.tankQuantity}
            onChange={handleChange}
          />

          <input
            style={styles.input}
            name="tankMeasures"
            placeholder="Medidas"
            value={form.tankMeasures}
            onChange={handleChange}
          />

          {renderSelect(
            "tankMaterial",
            "Seleccione material",
            tankMaterialOptions
          )}

          {renderSelect(
            "chemicalProduct",
            "Seleccione producto químico",
            chemicalProductOptions
          )}

          {renderSelect(
            "systemType",
            "Seleccione sistema",
            systemTypeOptions
          )}

          {renderSelect(
            "toolUsed",
            "Seleccione herramienta",
            toolUsedOptions
          )}

          <textarea
            style={styles.textarea}
            name="observations"
            placeholder="Observaciones"
            value={form.observations}
            onChange={handleChange}
          />

          <button
            type="submit"
            style={styles.saveButton}
            disabled={saving}
          >
            {saving ? "Guardando..." : "Guardar"}
          </button>
        </form>

        {message && (
          <p style={styles.message}>
            {message}
          </p>
        )}

        <button
          style={styles.backButton}
          onClick={onBack}
        >
          Volver
        </button>
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
    background: "#1f2937",
    padding: "28px",
    borderRadius: "12px",
    width: "760px",
    color: "#fff"
  },

  title: {
    textAlign: "center",
    marginBottom: "18px"
  },

  form: {
    display: "flex",
    flexDirection: "column",
    gap: "10px"
  },

  input: {
    padding: "10px",
    borderRadius: "6px",
    border: "1px solid #6b7280",
    background: "#374151",
    color: "#ffffff",
    fontSize: "16px"
  },

  textarea: {
    padding: "10px",
    borderRadius: "6px",
    border: "1px solid #6b7280",
    background: "#374151",
    color: "#ffffff",
    fontSize: "16px",
    minHeight: "70px"
  },

  saveButton: {
    padding: "10px",
    border: "none",
    borderRadius: "6px",
    background: "#2563eb",
    color: "#fff",
    fontWeight: "bold",
    cursor: "pointer"
  },

  backButton: {
    display: "block",
    margin: "12px auto 0",
    padding: "8px 14px",
    border: "none",
    borderRadius: "6px",
    background: "#6b7280",
    color: "#fff",
    cursor: "pointer"
  },

  message: {
    textAlign: "center",
    fontSize: "18px",
    color: "#d1d5db"
  }
};