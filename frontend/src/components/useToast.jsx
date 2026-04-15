import { createContext, useContext, useState, useCallback } from "react";

const ToastContext = createContext(null);

export function ToastProvider({ children }) {
  const [toasts, setToasts] = useState([]);

  const toast = useCallback(({ message, type = "info", duration = 3500 }) => {
    const id = Date.now() + Math.random();
    setToasts(prev => [...prev, { id, message, type, exiting: false }]);

    setTimeout(() => {
      setToasts(prev =>
        prev.map(t => t.id === id ? { ...t, exiting: true } : t)
      );
      setTimeout(() => {
        setToasts(prev => prev.filter(t => t.id !== id));
      }, 350);
    }, duration);
  }, []);

  const dismiss = useCallback((id) => {
    setToasts(prev => prev.map(t => t.id === id ? { ...t, exiting: true } : t));
    setTimeout(() => setToasts(prev => prev.filter(t => t.id !== id)), 350);
  }, []);

  return (
    <ToastContext.Provider value={toast}>
      {children}
      <ToastContainer toasts={toasts} dismiss={dismiss} />
    </ToastContext.Provider>
  );
}

export function useToast() {
  const ctx = useContext(ToastContext);
  if (!ctx) throw new Error("useToast must be used inside <ToastProvider>");
  return ctx;
}

const ICONS = {
  success: (
    <svg width="18" height="18" viewBox="0 0 18 18" fill="none">
      <circle cx="9" cy="9" r="9" fill="currentColor" opacity="0.15"/>
      <path d="M5 9l3 3 5-5" stroke="currentColor" strokeWidth="1.8" strokeLinecap="round" strokeLinejoin="round"/>
    </svg>
  ),
  error: (
    <svg width="18" height="18" viewBox="0 0 18 18" fill="none">
      <circle cx="9" cy="9" r="9" fill="currentColor" opacity="0.15"/>
      <path d="M6 6l6 6M12 6l-6 6" stroke="currentColor" strokeWidth="1.8" strokeLinecap="round"/>
    </svg>
  ),
  warning: (
    <svg width="18" height="18" viewBox="0 0 18 18" fill="none">
      <circle cx="9" cy="9" r="9" fill="currentColor" opacity="0.15"/>
      <path d="M9 5.5v4M9 12.5v.5" stroke="currentColor" strokeWidth="1.8" strokeLinecap="round"/>
    </svg>
  ),
  info: (
    <svg width="18" height="18" viewBox="0 0 18 18" fill="none">
      <circle cx="9" cy="9" r="9" fill="currentColor" opacity="0.15"/>
      <path d="M9 8v5M9 5.5v.5" stroke="currentColor" strokeWidth="1.8" strokeLinecap="round"/>
    </svg>
  ),
};

const COLORS = {
  success: { color: "#16a34a", bg: "#f0fdf4", border: "#bbf7d0" },
  error:   { color: "#dc2626", bg: "#fef2f2", border: "#fecaca" },
  warning: { color: "#d97706", bg: "#fffbeb", border: "#fde68a" },
  info:    { color: "#2563eb", bg: "#eff6ff", border: "#bfdbfe" },
};

function ToastContainer({ toasts, dismiss }) {
  return (
    <div style={{
      position: "fixed",
      bottom: "24px",
      right: "24px",
      zIndex: 9999,
      display: "flex",
      flexDirection: "column",
      gap: "10px",
      alignItems: "flex-end",
      pointerEvents: "none",
    }}>
      {toasts.map(t => (
        <Toast key={t.id} toast={t} dismiss={dismiss} />
      ))}
    </div>
  );
}

function Toast({ toast, dismiss }) {
  const { color, bg, border } = COLORS[toast.type] || COLORS.info;

  return (
    <div
      style={{
        pointerEvents: "all",
        display: "flex",
        alignItems: "center",
        gap: "10px",
        padding: "12px 16px",
        borderRadius: "10px",
        background: bg,
        border: `1px solid ${border}`,
        boxShadow: "0 4px 16px rgba(0,0,0,0.10)",
        minWidth: "260px",
        maxWidth: "380px",
        color: "#111827",
        fontFamily: "system-ui, Avenir, Helvetica, Arial, sans-serif",
        fontSize: "14px",
        lineHeight: "1.4",
        animation: toast.exiting
          ? "toast-out 0.35s cubic-bezier(0.4,0,1,1) forwards"
          : "toast-in 0.35s cubic-bezier(0,0,0.2,1) forwards",
      }}
    >
      <span style={{ color, flexShrink: 0 }}>{ICONS[toast.type]}</span>
      <span style={{ flex: 1 }}>{toast.message}</span>
      <button
        onClick={() => dismiss(toast.id)}
        style={{
          background: "none",
          border: "none",
          cursor: "pointer",
          padding: "2px",
          color: "#9ca3af",
          flexShrink: 0,
          lineHeight: 1,
          fontSize: "16px",
        }}
        aria-label="Dismiss"
      >
        ×
      </button>
      <style>{`
        @keyframes toast-in {
          from { opacity: 0; transform: translateY(12px) scale(0.97); }
          to   { opacity: 1; transform: translateY(0)    scale(1); }
        }
        @keyframes toast-out {
          from { opacity: 1; transform: translateY(0)    scale(1); }
          to   { opacity: 0; transform: translateY(8px)  scale(0.97); }
        }
      `}</style>
    </div>
  );
}