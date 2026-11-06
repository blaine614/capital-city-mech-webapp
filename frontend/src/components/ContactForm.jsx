import { useState } from "react";
import { postJson } from "../lib/api.js";

const EMPTY = {
  name: "",
  email: "",
  phone: "",
  propertyType: "RESIDENTIAL",
  serviceType: "",
  urgency: "SCHEDULED",
  message: "",
};

const SERVICE_OPTIONS = [
  "Plumbing repair",
  "Water heater",
  "Drain cleaning",
  "Excavation",
  "Backflow testing",
  "New construction / build-out",
  "Something else",
];

export default function ContactForm() {
  const [values, setValues] = useState(EMPTY);
  const [errors, setErrors] = useState({});
  const [state, setState] = useState("idle"); // idle | sending | sent | failed
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
    if (!values.name.trim()) found.name = "Enter the name we should ask for.";
    if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(values.email))
      found.email = "Enter an email address we can reply to.";
    if (values.phone.replace(/\D/g, "").length < 10)
      found.phone = "Enter a 10-digit phone number.";
    if (!values.serviceType) found.serviceType = "Pick the closest match.";
    if (values.message.trim().length < 10)
      found.message = "Tell us a little about the job — 10 characters minimum.";
    return found;
  }

  async function handleSubmit() {
    const found = validate();
    setErrors(found);
    if (Object.keys(found).length > 0) return;

    setState("sending");
    try {
      await postJson("/api/contact", values);
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
        <h4>Request received</h4>
        <p>
          A dispatcher will call you back during business hours. If water is
          moving where it should not be, call{" "}
          <a href="tel:+16148718100">(614) 871-8100</a> instead of waiting.
        </p>
        <button className="btn btn--ghost" onClick={() => setState("idle")}>
          Send another request
        </button>
      </div>
    );
  }

  return (
    <div className="reqform">
      {state === "failed" && (
        <div className="form-status form-status--fail" role="alert">
          <strong>The request did not send.</strong>
          <p>
            {failMessage} Call <a href="tel:+16148718100">(614) 871-8100</a> and
            we will take the details over the phone.
          </p>
        </div>
      )}

      <div className="reqform__row">
        <Field
          label="Name"
          error={errors.name}
          value={values.name}
          onChange={update("name")}
          autoComplete="name"
        />
        <Field
          label="Phone"
          type="tel"
          error={errors.phone}
          value={values.phone}
          onChange={update("phone")}
          autoComplete="tel"
        />
      </div>

      <Field
        label="Email"
        type="email"
        error={errors.email}
        value={values.email}
        onChange={update("email")}
        autoComplete="email"
      />

      <div className="reqform__row">
        <label className="field">
          <span>Property type</span>
          <select value={values.propertyType} onChange={update("propertyType")}>
            <option value="RESIDENTIAL">Residential</option>
            <option value="COMMERCIAL">Commercial</option>
            <option value="INDUSTRIAL">Industrial</option>
          </select>
        </label>

        <label className={`field ${errors.serviceType ? "field--error" : ""}`}>
          <span>What do you need</span>
          <select value={values.serviceType} onChange={update("serviceType")}>
            <option value="">Choose one</option>
            {SERVICE_OPTIONS.map((o) => (
              <option key={o} value={o}>
                {o}
              </option>
            ))}
          </select>
          {errors.serviceType && (
            <em className="field-error">{errors.serviceType}</em>
          )}
        </label>
      </div>

      <label className="field">
        <span>How soon</span>
        <select value={values.urgency} onChange={update("urgency")}>
          <option value="EMERGENCY">Emergency — active leak or no water</option>
          <option value="URGENT">This week</option>
          <option value="SCHEDULED">Scheduled work</option>
          <option value="QUOTE">Bidding a project</option>
        </select>
      </label>

      <label className={`field ${errors.message ? "field--error" : ""}`}>
        <span>Details</span>
        <textarea
          value={values.message}
          onChange={update("message")}
          placeholder="Where the problem is, what you have already tried, and any access notes for the crew."
        />
        {errors.message && <em className="field-error">{errors.message}</em>}
      </label>

      <button
        className="btn btn--primary reqform__submit"
        onClick={handleSubmit}
        disabled={state === "sending"}
      >
        {state === "sending" ? "Sending…" : "Send request"}
      </button>

      <p className="form-note">
        We use your details to schedule and quote the work. Nothing gets sold or
        shared.
      </p>

      <style>{`
        .reqform__row {
          display: grid;
          gap: 0 1rem;
          grid-template-columns: repeat(auto-fit, minmax(min(100%, 13rem), 1fr));
        }
        .reqform__submit { width: 100%; margin-top: 0.5rem; }
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
