import { useCallback, useEffect, useState } from "react";
import { getPortalToken, portalRequest, setPortalToken } from "../../lib/api.js";
import LoginPanel from "./LoginPanel.jsx";
import ProfileView from "./ProfileView.jsx";
import TeamAdmin from "./TeamAdmin.jsx";
import { Notice } from "./fields.jsx";

// The whole portal on one page: sign-in when signed out, the employee's
// profile when signed in, plus a Team tab for office admins.
export default function PortalApp({ officePhone }) {
  const [state, setState] = useState("loading"); // loading | signedOut | signedIn | unreachable
  const [profile, setProfile] = useState(null);
  const [notice, setNotice] = useState("");
  const [tab, setTab] = useState("profile");

  const load = useCallback(async () => {
    if (!getPortalToken()) {
      setState("signedOut");
      return;
    }
    setState("loading");
    try {
      setProfile(await portalRequest("GET", "/api/portal/me"));
      setState("signedIn");
    } catch (err) {
      if (err.status === 401) {
        setPortalToken(null);
        setNotice("Your session has ended. Sign in again.");
        setState("signedOut");
      } else {
        setNotice(err.message);
        setState("unreachable");
      }
    }
  }, []);

  useEffect(() => {
    load();
  }, [load]);

  // Any request that comes back 401 lands here.
  const onExpired = useCallback(() => {
    setPortalToken(null);
    setProfile(null);
    setTab("profile");
    setNotice("Your session has ended. Sign in again.");
    setState("signedOut");
  }, []);

  function onSignedIn(session) {
    setPortalToken(session.token);
    setProfile(session.profile);
    setNotice("");
    setTab("profile");
    setState("signedIn");
  }

  async function signOut() {
    try {
      await portalRequest("POST", "/api/portal/auth/logout");
    } catch {
      // Signing out locally is enough if the server cannot be reached.
    }
    setPortalToken(null);
    setProfile(null);
    setNotice("");
    setState("signedOut");
  }

  if (state === "loading") {
    return <p className="dim" role="status">Loading the portal…</p>;
  }

  if (state === "unreachable") {
    return (
      <div className="portal-narrow">
        <Notice kind="fail">{notice}</Notice>
        <button type="button" className="btn btn--ghost" onClick={load}>
          Try again
        </button>
      </div>
    );
  }

  if (state === "signedOut") {
    return <LoginPanel notice={notice} officePhone={officePhone} onSignedIn={onSignedIn} />;
  }

  const isAdmin = profile.role === "ADMIN";

  return (
    <div>
      <div className="portal-top">
        <div>
          <h2>
            {profile.firstName} {profile.lastName}
          </h2>
          <p className="portal-top__sub">
            {[profile.jobTitle, profile.department].filter(Boolean).join(" · ") ||
              "Capital City Mechanical"}
          </p>
        </div>
        <button type="button" className="btn btn--ghost btn--small" onClick={signOut}>
          Sign out
        </button>
      </div>

      {isAdmin && (
        <div className="portal-tabs" role="tablist" aria-label="Portal sections">
          <button
            type="button"
            role="tab"
            aria-selected={tab === "profile"}
            onClick={() => setTab("profile")}
          >
            My profile
          </button>
          <button
            type="button"
            role="tab"
            aria-selected={tab === "team"}
            onClick={() => setTab("team")}
          >
            Team
          </button>
        </div>
      )}

      {tab === "team" && isAdmin ? (
        <TeamAdmin me={profile} onExpired={onExpired} onSelfChanged={setProfile} />
      ) : (
        <ProfileView
          profile={profile}
          officePhone={officePhone}
          onSaved={setProfile}
          onExpired={onExpired}
        />
      )}
    </div>
  );
}
