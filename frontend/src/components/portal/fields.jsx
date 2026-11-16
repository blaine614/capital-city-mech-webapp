// Small shared pieces for the portal forms.

export function Field({ label, error, hint, ...rest }) {
  return (
    <label className={`field ${error ? "field--error" : ""}`}>
      <span>{label}</span>
      <input {...rest} />
      {hint && !error && <em className="form-note">{hint}</em>}
      {error && <em className="field-error">{error}</em>}
    </label>
  );
}

export function Notice({ kind = "info", children }) {
  if (!children) return null;
  const cls =
    kind === "fail" ? "form-status--fail" : kind === "ok" ? "form-status--ok" : "";
  return (
    <div className={`form-status ${cls}`} role={kind === "fail" ? "alert" : "status"}>
      <p style={{ margin: 0 }}>{children}</p>
    </div>
  );
}

// "2027-03-01" -> "Mar 1, 2027", without the timezone shifting the day.
export function formatDate(iso) {
  if (!iso) return "";
  const [y, m, d] = iso.split("-").map(Number);
  return new Date(y, m - 1, d).toLocaleDateString("en-US", {
    year: "numeric",
    month: "short",
    day: "numeric",
  });
}

// Whole days from today until an ISO date. Negative once it has passed.
export function daysUntil(iso) {
  const [y, m, d] = iso.split("-").map(Number);
  const now = new Date();
  const today = new Date(now.getFullYear(), now.getMonth(), now.getDate());
  return Math.round((new Date(y, m - 1, d) - today) / 86400000);
}

export function LicenseList({ licenses, onRemove, busy }) {
  if (!licenses || licenses.length === 0) {
    return <p className="dim" style={{ margin: 0 }}>None on file.</p>;
  }
  return (
    <ul className="licenses">
      {licenses.map((l) => {
        const left = l.expiresOn ? daysUntil(l.expiresOn) : null;
        let chip = <span className="chip">No expiration</span>;
        if (left !== null) {
          if (left < 0) chip = <span className="chip chip--bad">Expired {formatDate(l.expiresOn)}</span>;
          else if (left <= 60)
            chip = (
              <span className="chip chip--warn">
                Expires in {left} {left === 1 ? "day" : "days"}
              </span>
            );
          else chip = <span className="chip chip--ok">Expires {formatDate(l.expiresOn)}</span>;
        }
        const detail = [l.issuer, l.licenseNumber && `No. ${l.licenseNumber}`]
          .filter(Boolean)
          .join(" · ");
        return (
          <li key={l.id}>
            <div>
              <strong>{l.name}</strong>
              {detail && <small>{detail}</small>}
            </div>
            <div className="team__side">
              {chip}
              {onRemove && (
                <button
                  type="button"
                  className="portal-link"
                  disabled={busy}
                  onClick={() => onRemove(l)}
                >
                  Remove<span className="visually-hidden"> {l.name}</span>
                </button>
              )}
            </div>
          </li>
        );
      })}
    </ul>
  );
}
