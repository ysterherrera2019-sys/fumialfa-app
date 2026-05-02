import { useState } from "react";
import api from "../api"; // 🔥 usar proxy

export default function LoginPage({ onLogin }) {
  const [username, setUsername] = useState("admin");
  const [password, setPassword] = useState("admin123");
  const [message, setMessage] = useState("");

  const handleLogin = async (e) => {
    e.preventDefault();

    try {
      const res = await api.post("/api/auth/login", {
        username,
        password
      });

      localStorage.setItem("token", res.data.token);
      onLogin();
    } catch (err) {
      console.error(err);
      setMessage("Usuario o clave incorrectos");
    }
  };

  return (
    <div style={styles.page}>
      <form onSubmit={handleLogin} style={styles.card}>
        <h1 style={styles.title}>Ingreso Operador</h1>

        <input
          style={styles.input}
          placeholder="Usuario"
          value={username}
          onChange={(e) => setUsername(e.target.value)}
        />

        <input
          style={styles.input}
          type="password"
          placeholder="Clave"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
        />

        <button style={styles.button}>Ingresar</button>

        {message && <p style={styles.message}>{message}</p>}
      </form>
    </div>
  );
}

const styles = {
  page: {
    minHeight: "100vh",
    background: "#111827",
    display: "flex",
    alignItems: "center",
    justifyContent: "center"
  },
  card: {
    width: "360px",
    background: "#1f2937",
    padding: "28px",
    borderRadius: "12px",
    display: "flex",
    flexDirection: "column",
    gap: "14px"
  },
  title: {
    color: "#fff",
    textAlign: "center"
  },
  input: {
    padding: "12px",
    borderRadius: "6px",
    border: "1px solid #6b7280",
    background: "#374151",
    color: "#fff"
  },
  button: {
    padding: "12px",
    border: "none",
    borderRadius: "6px",
    background: "#2563eb",
    color: "#fff",
    fontWeight: "bold",
    cursor: "pointer"
  },
  message: {
    color: "#f87171",
    textAlign: "center"
  }
};