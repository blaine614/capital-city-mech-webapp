import { useState } from "react";
import { portalRequest } from "../../lib/api.js";
import { Field, LicenseList, Notice, formatDate } from "./fields.jsx";

const EDITABLE = [
  "firstName",
  "lastName",
  "phone",
  "addressLine1",
  "addressLine2",
  "city",
  "state",
  "postalCode",
  "emergencyName",
  "emergencyRelationship",
  "emergencyPhone",
];

function toForm(profile) {
  const form = {};
  for (const key of EDITABLE) form[key] = profile[key] ?? "";
  return form;
}

// An employee's own profile. Contact details and the emergency contact are
// theirs to edit; job details and licenses are kept by the office.
export default function ProfileView({ profile, officePhone, onSaved, onExpired }) {
  const [values, setValues] = useState(() => toForm(profile));
  const [errors, setErrors] = useState({});
  const [state, setState] = useState("idle"); // idle | saving | saved | failed
  const [failMessage, setFailMessage] = useState("");

  function bind(field) {
    return {
      value: values[field],
      error: errors[field],
      onChange: (e) => {
        setValues((v) => ({ ...v, [field]: e.target.value }));
        if (state !== "idle") setState("idle");
      },
    };
  }

  async function save(e) {
    e.preventDefault();
    setState("saving");
    setErrors({});
    try {
      const updated = await portalRequest("PUT", "/api/portal/me", values);
      setValues(toForm(updated));
      onSaved(updated);
      setState("saved");
    } catch (err) {
      if (err.status === 401) return onExpired();
      setFailMessage(err.message);
      setErrors(err.fieldErrors || {});
      setState("failed");
    }
  }

  const job = [
    ["Job title", profile.jobTitle],
    ["Department", profile.department],
    ["Hire date", formatDate(profile.hireDate)],
    ["Supervisor", profile.supervisor],
  ];

  return (
    <div className="portal-grid">
      <form onSubmit={save} noValidate>
        <section className="pcard">
          <h3>Contact details</h3>
          {state === "failed" && <Notice kind="fail">{failMessage}</Notice>}
          <div className="portal-row">
            <Field label="First name" autoComplete="given-name" {...bind("firstName")} />
            <Field label="Last name" autoComplete="family-name" {...bind("lastName")} />
          </div>
          <div className="portal-row">
            <Field label="Phone" type="tel" autoComplete="tel" {...bind("phone")} />
            <Field
              label="Email (sign-in)"
              type="email"
              value={profile.email}
              readOnly
              hint={`To change it, call the office at ${officePhone}.`}
            />
          </div>
          <Field label="Street address" autoComplete="address-line1" {...bind("addressLine1")} />
          <Field label="Apt, unit, or PO box" autoComplete="address-line2" {...bind("addressLine2")} />
          <div className="portal-row">
            <Field label="City" autoComplete="address-level2" {...bind("city")} />
            <Field label="State" autoComplete="address-level1" {...bind("state")} />
            <Field label="ZIP" autoComplete="postal-code" inputMode="numeric" {...bind("postalCode")} />
          </div>
        </section>

        <section className="pcard">
          <h3>Emergency contact</h3>
          <p className="pcard__hint">Who the office should call if something happens on a job.</p>
          <div className="portal-row">
            <Field label="Name" {...bind("emergencyName")} />
            <Field label="Relationship" {...bind("emergencyRelationship")} />
          </div>
          <Field label="Phone" type="tel" {...bind("emergencyPhone")} />
        </section>

        <div className="portal-actions" style={{ marginTop: "1.25rem" }}>
          <button className="btn btn--primary" disabled={state === "saving"}>
            {state === "saving" ? "Saving…" : "Save changes"}
          </button>
          {state === "saved" && (
            <span className="portal-saved" role="status">
              Saved.
            </span>
          )}
        </div>
      </form>

      <div>
        <section className="pcard">
          <h3>Job details</h3>
          <dl className="facts">
            {job.map(([label, value]) => (
              <div key={label}>
                <dt>{label}</dt>
                <dd className={value ? "" : "is-empty"}>{value || "Not on file"}</dd>
              </div>
            ))}
          </dl>
        </section>

        <section className="pcard">
          <h3>Licenses &amp; certifications</h3>
          <LicenseList licenses={profile.licenses} />
        </section>

        <p className="form-note" style={{ marginTop: "1rem" }}>
          Job details and licenses are kept by the office. If something is wrong or
          missing, call {officePhone}.
        </p>
      </div>
    </div>
  );
}
