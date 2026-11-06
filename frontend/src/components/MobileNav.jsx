import { useEffect, useRef, useState } from "react";

export default function MobileNav({ links, phone, phoneHref }) {
  const [open, setOpen] = useState(false);
  const panelRef = useRef(null);
  const triggerRef = useRef(null);

  useEffect(() => {
    document.body.style.overflow = open ? "hidden" : "";
    return () => {
      document.body.style.overflow = "";
    };
  }, [open]);

  useEffect(() => {
    function onKey(e) {
      if (e.key === "Escape" && open) {
        setOpen(false);
        triggerRef.current?.focus();
      }
    }
    window.addEventListener("keydown", onKey);
    return () => window.removeEventListener("keydown", onKey);
  }, [open]);

  useEffect(() => {
    if (open) panelRef.current?.querySelector("a")?.focus();
  }, [open]);

  return (
    <div className="mnav">
      <button
        ref={triggerRef}
        className="mnav__trigger"
        aria-expanded={open}
        aria-controls="mobile-menu"
        onClick={() => setOpen((v) => !v)}
      >
        <span className="visually-hidden">
          {open ? "Close menu" : "Open menu"}
        </span>
        <span className={`mnav__bars ${open ? "is-open" : ""}`} aria-hidden="true">
          <i />
          <i />
          <i />
        </span>
      </button>

      {open && (
        <div className="mnav__panel" id="mobile-menu" ref={panelRef}>
          <nav aria-label="Mobile">
            {links.map((link) => (
              <a key={link.href} href={link.href} onClick={() => setOpen(false)}>
                {link.label}
              </a>
            ))}
          </nav>
          <a className="btn btn--primary mnav__call" href={phoneHref}>
            Call {phone}
          </a>
          <img
            className="mnav__logo"
            src="/logo.png"
            alt=""
            width="720"
            height="276"
          />
        </div>
      )}

      <style>{`
        .mnav { display: none; }
        @media (max-width: 900px) { .mnav { display: block; } }

        .mnav__trigger {
          background: transparent;
          border: 1px solid var(--line);
          border-radius: 2px;
          padding: 0.6rem;
          cursor: pointer;
          display: grid;
          place-items: center;
        }

        .mnav__bars { display: grid; gap: 5px; width: 22px; }
        .mnav__bars i {
          display: block;
          height: 2px;
          background: var(--paper);
          transition: transform 0.2s ease, opacity 0.2s ease;
        }
        .mnav__bars.is-open i:nth-child(1) { transform: translateY(7px) rotate(45deg); }
        .mnav__bars.is-open i:nth-child(2) { opacity: 0; }
        .mnav__bars.is-open i:nth-child(3) { transform: translateY(-7px) rotate(-45deg); }

        .mnav__panel {
          position: fixed;
          inset: auto 0 0 0;
          top: 7.5rem;
          background: var(--ink);
          border-top: 1px solid var(--line);
          padding: 1.5rem var(--gutter) 2.5rem;
          display: flex;
          flex-direction: column;
          gap: 1.5rem;
          overflow-y: auto;
        }

        .mnav__panel nav { display: flex; flex-direction: column; }

        .mnav__panel nav a {
          font-family: var(--display);
          font-size: 1.9rem;
          font-weight: 600;
          letter-spacing: 0.06em;
          text-transform: uppercase;
          text-decoration: none;
          color: var(--paper);
          padding: 0.85rem 0;
          border-bottom: 1px solid var(--line-soft);
        }

        .mnav__call { width: 100%; }

        /* Decorative here — the header already names the company. */
        .mnav__logo {
          width: 100%;
          max-width: 13rem;
          height: auto;
          opacity: 0.5;
          margin-top: auto;
        }
      `}</style>
    </div>
  );
}
