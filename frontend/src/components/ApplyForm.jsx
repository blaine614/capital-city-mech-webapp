import { useState } from "react";
import { postJson } from "../lib/api.js";

const EMPTY = {
  jobPostingId: "",
  firstName: "",
  lastName: "",
  email: "",
  phone: "",
  yearsExperience: "",
  licenses: "",
  resumeUrl: "",
  coverNote: "",
};

export default function ApplyForm({ postings = [] }) {
  const [values, setValues] = useState(EMPTY);
  const [errors, setErrors] = useState({});
  const [state, setState] = useState("idle");
  const [failMessage, setFailMessage] = useState("");

  function update(field) {
    return (e) => {
      setValues((v) => ({ ...v, [field]: e.target.value }));
      setErrors((prev) => {
        if (!prev[field]) return prev;
        const next = { ...prev };
        delete next[field];
        return next;
      });
    };
  }

  function validate() {
    const found = {};
    if (!values.jobPostingId) found.jobPostingId = "Pick the role you want.";
    if (!values.firstName.trim()) found.firstName = "Enter your first name.";
    if (!values.lastName.trim()) found.lastName = "Enter your last name.";
    if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(values.email))
      found.email = "Enter an email address we can reach you at.";
    if (values.phone.replace(/\D/g, "").length < 10)
      found.phone = "Enter a 10-digit phone number.";
    if (values.yearsExperience === "" || Number(values.yearsExperience) < 0)
      found.yearsExperience = "Enter years in the trade — 0 is fine.";
    return found;
  }

  async function handleSubmit() {
    const found = validate();
    setErrors(found);
    if (Object.keys(found).length > 0) return;

    setState("sending");
    try {
      await postJson("/api/careers/applications", {
        ...values,
        jobPostingId: Number(values.jobPostingId),
        yearsExperience: Number(values.yearsExperience),
      });
      setState("sent");
      setValues(EMPTY);
    } catch (err) {
      setState("failed");
      setFailMessage(err.message);
      if (err.fieldErrors) setErrors(err.fieldErrors);
    }
  }

  if (state === "sent") {
    return (
      <div className="form-status form-status--ok" role="status">
        <h4>Application received</h4>
        <p>
          Our field manager reviews applications weekly. If it is a fit, you will
          hear from us to set up a shop visit.
        </p>
        <button className="btn btn--ghost" onClick={() => setState("idle")}>
          Apply for another role
        </button>
      </div>
    );
  }

  return (
    <div className="apply">
      {state === "failed" && (
        <div className="form-status form-status--fail" role="alert">
          <strong>The application did not send.</strong>
          <p>{failMessage} Try again, or call the shop and ask for hiring.</p>
        </div>
      )}

      <label className={`field ${errors.jobPostingId ? "field--error" : ""}`}>
        <span>Role</span>
        <select value={values.jobPostingId} onChange={update("jobPostingId")}>
          <option value="">Choose a role</option>
          {postings.map((p) => (
            <option key={p.id} value={p.id}>
              {p.title} — {p.location}
            </option>
          ))}
        </select>
        {errors.jobPostingId && (
          <em className="field-error">{errors.jobPostingId}</em>
        )}
      </label>

      <div className="apply__row">
        <Field
          label="First name"
          value={values.firstName}
          onChange={update("firstName")}
          error={errors.firstName}
          autoComplete="given-name"
        />
        <Field
          label="Last name"
          value={values.lastName}
          onChange={update("lastName")}
          error={errors.lastName}
          autoComplete="family-name"
        />
      </div>

      <div className="apply__row">
        <Field
          label="Email"
          type="email"
          value={values.email}
          onChange={update("email")}
          error={errors.email}
          autoComplete="email"
        />
        <Field
          label="Phone"
          type="tel"
          value={values.phone}
          onChange={update("phone")}
          error={errors.phone}
          autoComplete="tel"
        />
      </div>

      <div className="apply__row">
        <Field
          label="Years in the trade"
          type="number"
          min="0"
          max="60"
          value={values.yearsExperience}
          onChange={update("yearsExperience")}
          error={errors.yearsExperience}
        />
        <Field
          label="Licenses or cards"
          value={values.licenses}
          onChange={update("licenses")}
          placeholder="Journeyman, OSHA 30, CDL…"
        />
      </div>

      <Field
        label="Link to resume (optional)"
        type="url"
        value={values.resumeUrl}
        onChange={update("resumeUrl")}
        placeholder="https://"
      />

      <label className="field">
        <span>Anything we should know</span>
        <textarea
          value={values.coverNote}
          onChange={update("coverNote")}
          placeholder="The kind of work you have run, crews you have led, or what you want to learn next."
        />
      </label>

      <button
        className="btn btn--primary apply__submit"
        onClick={handleSubmit}
        disabled={state === "sending"}
      >
        {state === "sending" ? "Sending…" : "Submit application"}
      </button>

      <style>{`
        .apply__row {
          display: grid;
          gap: 0 1rem;
          grid-template-columns: repeat(auto-fit, minmax(min(100%, 13rem), 1fr));
        }
        .apply__submit { width: 100%; margin-top: 0.5rem; }
        .form-status h4 { margin-bottom: 0.5rem; }
        .form-status .btn { margin-top: 0.5rem; }
      `}</style>
    </div>
  );
}

function Field({ label, error, ...rest }) {
  return (
    <label className={`field ${error ? "field--error" : ""}`}>
      <span>{label}</span>
      <input {...rest} />
      {error && <em className="field-error">{error}</em>}
    </label>
  );
}
