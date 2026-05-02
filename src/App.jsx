import { useState } from "react";
import LoginPage from "./components/LoginPage";
import OperatorDashboard from "./components/OperatorDashboard";
import ClientRegistrationForm from "./components/ClientRegistrationForm";

function App() {
  const [view, setView] = useState("home");
  const [isLoggedIn, setIsLoggedIn] = useState(
    !!localStorage.getItem("token")
  );

  const logout = () => {
    localStorage.removeItem("token");
    setIsLoggedIn(false);
    setView("home");
  };

  // OPERADOR LOGUEADO
  if (isLoggedIn && view === "operator") {
    return <OperatorDashboard onLogout={logout} />;
  }

  // LOGIN OPERADOR
  if (view === "login") {
    return (
      <LoginPage
        onLogin={() => {
          setIsLoggedIn(true);
          setView("operator");
        }}
      />
    );
  }

  // REGISTRO CLIENTE
  if (view === "client") {
    return <ClientRegistrationForm />;
  }

  // PANTALLA INICIAL
  return (
    <div style={styles.page}>
      <div style={styles.card}>
        <h1 style={styles.title}>FUMIALFA</h1>

        <button
          style={styles.buttonPrimary}
          onClick={() => setView("client")}
        >
          Registro de Cliente
        </button>

        <button
          style={styles.buttonSecondary}
          onClick={() => setView("login")}
        >
          Ingreso Operador
        </button>
      </div>
    </div>
  );
}

export default App;

const styles = {
  page: {
    minHeight: "100vh",
    background: "#111827",
    display: "flex",
    justifyContent: "center",
    alignItems: "center"
  },
  card: {
    background: "#1f2937",
    padding: "40px",
    borderRadius: "12px",
    display: "flex",
    flexDirection: "column",
    gap: "20px",
    width: "320px"
  },
  title: {
    color: "#fff",
    textAlign: "center"
  },
  buttonPrimary: {
    padding: "14px",
    border: "none",
    borderRadius: "8px",
    background: "#2563eb",
    color: "#fff",
    fontWeight: "bold",
    cursor: "pointer"
  },
  buttonSecondary: {
    padding: "14px",
    border: "none",
    borderRadius: "8px",
    background: "#6b7280",
    color: "#fff",
    fontWeight: "bold",
    cursor: "pointer"
  }
};