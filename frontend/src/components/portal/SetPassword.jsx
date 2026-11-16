import { useState } from "react";
import { portalRequest, setPortalToken } from "../../lib/api.js";
import { Field, Notice } from "./fields.jsx";

const MIN_LENGTH = 10;

// Landing page for the emailed invite / reset link: /portal/set-password?token=...
export default function SetPassword() {
  const [token] = useState(
    () => new URLSearchParams(window.location.search).get("token") || ""
  );
  const [password, setPassword] = useState("");
  const [confirm, setConfirm] = useState("");
  const [errors, setErrors] = useState({});
  const [state, setState] = useState("idle"); // idle | saving | done | failed
  const [failMessage, setFailMessage] = useState("");

  async function submit(e) {
    e.preventDefault();
    const found = {};
    if (password.length < MIN_LENGTH) found.password = `Use at least ${MIN_LENGTH} characters.`;
    else if (password !== confirm) found.confirm = "The two passwords do not match.";
    setErrors(found);
    if (Object.keys(found).length > 0) return;

    setState("saving");
    try {
      await portalRequest("POST", "/api/portal/auth/set-password", { token, password }, null);
      // A new password signs every device out, including this one.
      setPortalToken(null);
      setState("done");
    } catch (err) {
      setFailMessage(err.message);
      setErrors(err.fieldErrors || {});
      setState("failed");
    }
  }

  if (!token) {
    return (
      <div className="pcard portal-narrow">
        <h2>This link is incomplete</h2>
        <p>
          Open the link from your email again, or ask for a new one from the sign-in
          page.
        </p>
        <a className="btn btn--ghost" href="/portal">
          Go to sign in
        </a>
      </div>
    );
  }

  if (state === "done") {
    return (
      <div className="pcard portal-narrow">
        <h2>Password saved</h2>
        <p>You can sign in to the employee portal with your email and new password.</p>
        <a className="btn btn--primary" href="/portal">
          Sign in
        </a>
      </div>
    );
  }

  return (
    <form className="pcard portal-narrow" onSubmit={submit} noValidate>
      <h2>Choose a password</h2>
      {state === "failed" && (
        <Notice kind="fail">
          {failMessage} <a href="/portal">Go to sign in</a>
        </Notice>
      )}
      <Field
        label="New password"
        type="password"
        autoComplete="new-password"
        value={password}
        error={errors.password}
        hint={`At least ${MIN_LENGTH} characters. A few unrelated words works well.`}
        onChange={(e) => setPassword(e.target.value)}
      />
      <Field
        label="Type it again"
        type="password"
        autoComplete="new-password"
        value={confirm}
        error={errors.confirm}
        onChange={(e) => setConfirm(e.target.value)}
      />
      <button className="btn btn--primary" disabled={state === "saving"}>
        {state === "saving" ? "Saving…" : "Save password"}
      </button>
    </form>
  );
}
