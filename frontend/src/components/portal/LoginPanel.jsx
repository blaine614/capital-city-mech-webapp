import { useState } from "react";
import { portalRequest } from "../../lib/api.js";
import { Field, Notice } from "./fields.jsx";

// Sign-in form, plus the "email me a link" form used both for a first-time
// password and a forgotten one.
export default function LoginPanel({ notice, officePhone, onSignedIn }) {
  const [mode, setMode] = useState("signin"); // signin | link | linkSent
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [busy, setBusy] = useState(false);
  const [error, setError] = useState("");
  const [fieldErrors, setFieldErrors] = useState({});

  async function signIn(e) {
    e.preventDefault();
    setBusy(true);
    setError("");
    setFieldErrors({});
    try {
      onSignedIn(await portalRequest("POST", "/api/portal/auth/login", { email, password }, null));
    } catch (err) {
      setError(err.message);
      setFieldErrors(err.fieldErrors || {});
      setBusy(false);
    }
  }

  async function sendLink(e) {
    e.preventDefault();
    setBusy(true);
    setError("");
    setFieldErrors({});
    try {
      await portalRequest("POST", "/api/portal/auth/forgot", { email }, null);
      setMode("linkSent");
    } catch (err) {
      setError(err.message);
      setFieldErrors(err.fieldErrors || {});
    }
    setBusy(false);
  }

  function switchTo(next) {
    setMode(next);
    setError("");
    setFieldErrors({});
    setPassword("");
  }

  if (mode === "linkSent") {
    return (
      <div className="pcard portal-narrow">
        <h2>Check your email</h2>
        <p>
          If <strong>{email}</strong> is on file with the office, a link to set your
          password is on its way. It can take a couple of minutes, and it may land in
          junk mail.
        </p>
        <p className="dim">
          Nothing after a few minutes? Call the office at {officePhone} and ask them to
          check the email address on your employee record.
        </p>
        <button type="button" className="btn btn--ghost" onClick={() => switchTo("signin")}>
          Back to sign in
        </button>
      </div>
    );
  }

  if (mode === "link") {
    return (
      <form className="pcard portal-narrow" onSubmit={sendLink} noValidate>
        <h2>Get a password link</h2>
        <p className="pcard__hint">
          Enter the email address the office has for you. We will send a link to set
          your password, whether it is your first time here or you forgot it.
        </p>
        <Notice kind="fail">{error}</Notice>
        <Field
          label="Email"
          type="email"
          autoComplete="username"
          value={email}
          error={fieldErrors.email}
          onChange={(e) => setEmail(e.target.value)}
          required
        />
        <div className="portal-actions">
          <button className="btn btn--primary" disabled={busy}>
            {busy ? "Sending…" : "Email me a link"}
          </button>
          <button type="button" className="portal-link" onClick={() => switchTo("signin")}>
            Back to sign in
          </button>
        </div>
      </form>
    );
  }

  return (
    <form className="pcard portal-narrow" onSubmit={signIn} noValidate>
      <h2>Sign in</h2>
      {!error && <Notice>{notice}</Notice>}
      <Notice kind="fail">{error}</Notice>
      <Field
        label="Email"
        type="email"
        autoComplete="username"
        value={email}
        error={fieldErrors.email}
        onChange={(e) => setEmail(e.target.value)}
        required
      />
      <Field
        label="Password"
        type="password"
        autoComplete="current-password"
        value={password}
        error={fieldErrors.password}
        onChange={(e) => setPassword(e.target.value)}
        required
      />
      <div className="portal-actions">
        <button className="btn btn--primary" disabled={busy}>
          {busy ? "Signing in…" : "Sign in"}
        </button>
        <button type="button" className="portal-link" onClick={() => switchTo("link")}>
          First time here or forgot your password?
        </button>
      </div>
      <p className="form-note" style={{ marginTop: "1.25rem", marginBottom: 0 }}>
        For Capital City Mechanical employees. Accounts are created by the office.
      </p>
    </form>
  );
}
