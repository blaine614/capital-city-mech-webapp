import { useEffect, useState } from "react";
import { portalRequest } from "../../lib/api.js";
import { Field, LicenseList, Notice } from "./fields.jsx";

const BASE = "/api/portal/admin/employees";

const BLANK = {
  email: "",
  firstName: "",
  lastName: "",
  jobTitle: "",
  department: "",
  hireDate: "",
  supervisor: "",
  role: "EMPLOYEE",
  active: true,
};

const BLANK_LICENSE = { name: "", licenseNumber: "", issuer: "", expiresOn: "" };

function toForm(employee) {
  return {
    email: employee.email,
    firstName: employee.firstName,
    lastName: employee.lastName,
    jobTitle: employee.jobTitle ?? "",
    department: employee.department ?? "",
    hireDate: employee.hireDate ?? "",
    supervisor: employee.supervisor ?? "",
    role: employee.role,
    active: employee.active,
  };
}

function status(employee) {
  if (!employee.active) return <span className="chip chip--bad">Deactivated</span>;
  if (!employee.passwordSet) return <span className="chip chip--warn">Invite pending</span>;
  return <span className="chip chip--ok">Active</span>;
}

// Office-only tab: add employees, edit job details, manage licenses.
export default function TeamAdmin({ me, onExpired, onSelfChanged }) {
  const [team, setTeam] = useState(null);
  const [loadError, setLoadError] = useState("");
  const [selectedId, setSelectedId] = useState(null); // null | "new" | employee id
  const [flash, setFlash] = useState("");

  async function call(method, path, body) {
    try {
      return await portalRequest(method, path, body);
    } catch (err) {
      if (err.status === 401) onExpired();
      throw err;
    }
  }

  async function reload() {
    setLoadError("");
    try {
      setTeam(await call("GET", BASE));
    } catch (err) {
      setLoadError(err.message);
    }
  }

  useEffect(() => {
    reload();
  }, []);

  // Put a changed employee back into the list (and into "My profile" if it is me).
  function merge(updated) {
    setTeam((list) => {
      const exists = list.some((e) => e.id === updated.id);
      const next = exists
        ? list.map((e) => (e.id === updated.id ? updated : e))
        : [...list, updated];
      return next.sort((a, b) =>
        `${a.lastName} ${a.firstName}`.localeCompare(`${b.lastName} ${b.firstName}`)
      );
    });
    if (updated.id === me.id) onSelfChanged(updated);
  }

  if (loadError) {
    return (
      <div>
        <Notice kind="fail">{loadError}</Notice>
        <button type="button" className="btn btn--ghost" onClick={reload}>
          Try again
        </button>
      </div>
    );
  }

  if (!team) return <p className="dim" role="status">Loading the team…</p>;

  const selected =
    selectedId === "new" ? null : team.find((e) => e.id === selectedId) || null;

  return (
    <div className="portal-grid">
      <section className="pcard">
        <div className="portal-top" style={{ marginBottom: "0.5rem" }}>
          <h3 style={{ fontSize: "var(--step-2)" }}>Employees ({team.length})</h3>
          <button
            type="button"
            className="btn btn--primary btn--small"
            onClick={() => {
              setFlash("");
              setSelectedId("new");
            }}
          >
            Add employee
          </button>
        </div>
        <ul className="team">
          {team.map((e) => (
            <li key={e.id}>
              <div className="team__who">
                <strong>
                  {e.firstName} {e.lastName}
                  {e.role === "ADMIN" ? " (office)" : ""}
                </strong>
                <small>{[e.jobTitle, e.email].filter(Boolean).join(" · ")}</small>
              </div>
              <div className="team__side">
                {status(e)}
                <button
                  type="button"
                  className="btn btn--ghost btn--small"
                  aria-pressed={selectedId === e.id}
                  onClick={() => {
                    setFlash("");
                    setSelectedId(e.id);
                  }}
                >
                  Manage
                  <span className="visually-hidden">
                    {" "}
                    {e.firstName} {e.lastName}
                  </span>
                </button>
              </div>
            </li>
          ))}
        </ul>
      </section>

      <div>
        {flash && <Notice kind="ok">{flash}</Notice>}
        {selectedId === null && (
          <section className="pcard">
            <h3>Manage the team</h3>
            <p className="dim" style={{ margin: 0 }}>
              Choose <strong>Add employee</strong> to invite someone, or{" "}
              <strong>Manage</strong> to edit job details and licenses. New employees
              get an email with a link to set their own password.
            </p>
          </section>
        )}
        {selectedId === "new" && (
          <EmployeeForm
            key="new"
            call={call}
            onDone={(created) => {
              merge(created);
              setSelectedId(created.id);
              setFlash(`Invite emailed to ${created.email}.`);
            }}
            onCancel={() => setSelectedId(null)}
          />
        )}
        {selected && (
          <>
            <EmployeeForm
              key={selected.id}
              employee={selected}
              isSelf={selected.id === me.id}
              call={call}
              onDone={(updated) => {
                merge(updated);
                setFlash("Saved.");
              }}
              onFlash={setFlash}
              onCancel={() => setSelectedId(null)}
            />
            <LicenseEditor
              key={`lic-${selected.id}`}
              employee={selected}
              call={call}
              onChanged={(updated) => {
                merge(updated);
                setFlash("");
              }}
            />
          </>
        )}
      </div>
    </div>
  );
}

function EmployeeForm({ employee, isSelf = false, call, onDone, onFlash, onCancel }) {
  const isNew = !employee;
  const [values, setValues] = useState(() => (isNew ? BLANK : toForm(employee)));
  const [errors, setErrors] = useState({});
  const [busy, setBusy] = useState(false);
  const [failMessage, setFailMessage] = useState("");

  function bind(field) {
    return {
      value: values[field],
      error: errors[field],
      onChange: (e) => setValues((v) => ({ ...v, [field]: e.target.value })),
    };
  }

  async function save(e) {
    e.preventDefault();
    setBusy(true);
    setErrors({});
    setFailMessage("");
    const body = { ...values, hireDate: values.hireDate || null };
    try {
      const saved = isNew
        ? await call("POST", BASE, body)
        : await call("PUT", `${BASE}/${employee.id}`, body);
      onDone(saved);
    } catch (err) {
      setFailMessage(err.message);
      setErrors(err.fieldErrors || {});
    }
    setBusy(false);
  }

  async function sendLink() {
    setBusy(true);
    setFailMessage("");
    try {
      await call("POST", `${BASE}/${employee.id}/invite`);
      onFlash(
        employee.passwordSet
          ? `Password reset link emailed to ${employee.email}.`
          : `Invite emailed again to ${employee.email}.`
      );
    } catch (err) {
      setFailMessage(err.message);
    }
    setBusy(false);
  }

  return (
    <form className="pcard" onSubmit={save} noValidate>
      <h3>{isNew ? "Add an employee" : `${employee.firstName} ${employee.lastName}`}</h3>
      {isNew && (
        <p className="pcard__hint">
          They will get an email with a link to choose their password. The link lasts
          7 days.
        </p>
      )}
      <Notice kind="fail">{failMessage}</Notice>
      <div className="portal-row">
        <Field label="First name" {...bind("firstName")} />
        <Field label="Last name" {...bind("lastName")} />
      </div>
      <Field label="Email (sign-in)" type="email" {...bind("email")} />
      <div className="portal-row">
        <Field label="Job title" {...bind("jobTitle")} />
        <Field label="Department" {...bind("department")} />
      </div>
      <div className="portal-row">
        <Field label="Hire date" type="date" {...bind("hireDate")} />
        <Field label="Supervisor" {...bind("supervisor")} />
      </div>
      <label className={`field ${errors.role ? "field--error" : ""}`}>
        <span>Portal access</span>
        <select
          value={values.role}
          disabled={isSelf}
          onChange={(e) => setValues((v) => ({ ...v, role: e.target.value }))}
        >
          <option value="EMPLOYEE">Employee: sees only their own profile</option>
          <option value="ADMIN">Office admin: can add and edit employees</option>
        </select>
        {errors.role && <em className="field-error">{errors.role}</em>}
      </label>
      {!isNew && !isSelf && (
        <label className="field field--check">
          <input
            type="checkbox"
            checked={values.active}
            onChange={(e) => setValues((v) => ({ ...v, active: e.target.checked }))}
          />
          <span>Can sign in (untick when someone leaves)</span>
        </label>
      )}
      <div className="portal-actions">
        <button className="btn btn--primary btn--small" disabled={busy}>
          {busy ? "Working…" : isNew ? "Add and send invite" : "Save"}
        </button>
        {!isNew && employee.active && (
          <button
            type="button"
            className="btn btn--ghost btn--small"
            disabled={busy}
            onClick={sendLink}
          >
            {employee.passwordSet ? "Email a reset link" : "Resend invite"}
          </button>
        )}
        <button type="button" className="portal-link" onClick={onCancel}>
          Close
        </button>
      </div>
    </form>
  );
}

function LicenseEditor({ employee, call, onChanged }) {
  const [values, setValues] = useState(BLANK_LICENSE);
  const [errors, setErrors] = useState({});
  const [busy, setBusy] = useState(false);
  const [failMessage, setFailMessage] = useState("");

  function bind(field) {
    return {
      value: values[field],
      error: errors[field],
      onChange: (e) => setValues((v) => ({ ...v, [field]: e.target.value })),
    };
  }

  async function add(e) {
    e.preventDefault();
    setBusy(true);
    setErrors({});
    setFailMessage("");
    try {
      const updated = await call("POST", `${BASE}/${employee.id}/licenses`, {
        ...values,
        expiresOn: values.expiresOn || null,
      });
      setValues(BLANK_LICENSE);
      onChanged(updated);
    } catch (err) {
      setFailMessage(err.message);
      setErrors(err.fieldErrors || {});
    }
    setBusy(false);
  }

  async function remove(license) {
    setBusy(true);
    setFailMessage("");
    try {
      onChanged(await call("DELETE", `${BASE}/${employee.id}/licenses/${license.id}`));
    } catch (err) {
      setFailMessage(err.message);
    }
    setBusy(false);
  }

  return (
    <section className="pcard">
      <h3>Licenses &amp; certifications</h3>
      <Notice kind="fail">{failMessage}</Notice>
      <LicenseList licenses={employee.licenses} onRemove={remove} busy={busy} />
      <form onSubmit={add} noValidate style={{ marginTop: "1.25rem" }}>
        <Field label="Add a license or certification" placeholder="Ohio Plumbing License" {...bind("name")} />
        <div className="portal-row">
          <Field label="Number" {...bind("licenseNumber")} />
          <Field label="Issued by" {...bind("issuer")} />
        </div>
        <Field label="Expires" type="date" {...bind("expiresOn")} />
        <button className="btn btn--ghost btn--small" disabled={busy}>
          Add license
        </button>
      </form>
    </section>
  );
}
