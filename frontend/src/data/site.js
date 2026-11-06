export const NAV_LINKS = [
  { href: "/", label: "Home" },
  { href: "/services", label: "Services" },
  { href: "/careers", label: "Careers" },
  { href: "/about", label: "About" },
  { href: "/contact", label: "Contact" },
];

export const PHONE = "(614) 871-8100";
export const PHONE_HREF = "tel:+16148718100";
export const EMAIL = "office@capitalcitymech.com";

export const ADDRESS = {
  street: "5929 Haughn Road",
  city: "Grove City",
  state: "OH",
  zip: "43123",
};

export const HOURS = [
  { days: "Monday – Friday", time: "8:00 am – 5:00 pm" },
  { days: "Saturday – Sunday", time: "Emergency dispatch only" },
];

// Fallback copy used when the Spring Boot service list is unreachable at build time.
export const SERVICES = [
  {
    slug: "residential-plumbing",
    name: "Residential plumbing",
    category: "RESIDENTIAL",
    summary:
      "Repairs, repipes, water heaters, and fixture installs for houses across Central Ohio.",
    points: [
      "Water heater replacement and tankless conversion",
      "Whole-house repipe in copper and PEX",
      "Fixture, faucet, and sump pump installs",
      "Frozen and burst line repair",
    ],
  },
  {
    slug: "commercial-plumbing",
    name: "Commercial plumbing",
    category: "COMMERCIAL",
    summary:
      "Build-outs, tenant improvements, and scheduled maintenance for schools, offices, and retail.",
    points: [
      "New construction rough-in and trim",
      "Backflow testing and certification",
      "Grease interceptor service",
      "Planned maintenance agreements",
    ],
  },
  {
    slug: "industrial-mechanical",
    name: "Industrial mechanical",
    category: "INDUSTRIAL",
    summary:
      "Process piping and mechanical systems for manufacturing floors that cannot afford downtime.",
    points: [
      "Process and compressed air piping",
      "Certified industrial welding",
      "Equipment tie-ins and relocations",
      "Shutdown and turnaround support",
    ],
  },
  {
    slug: "drain-cleaning",
    name: "Drain cleaning",
    category: "ALL",
    summary:
      "Cabling, hydro jetting, and camera inspection to find the blockage instead of guessing at it.",
    points: [
      "High-pressure hydro jetting",
      "Sewer camera inspection and locating",
      "Root cutting and descaling",
      "Recurring line maintenance",
    ],
  },
  {
    slug: "excavation",
    name: "Excavation & hydro excavation",
    category: "ALL",
    summary:
      "Water and sewer line replacement, plus vacuum excavation where digging blind is not an option.",
    points: [
      "Water and sanitary service replacement",
      "Hydro excavation around live utilities",
      "Storm and site drainage",
      "Trenchless options where the site allows",
    ],
  },
  {
    slug: "backflow-testing",
    name: "Backflow & water quality",
    category: "ALL",
    summary:
      "Certification testing, repairs, and treatment so what comes out of the tap is what should.",
    points: [
      "Annual backflow testing and certification",
      "Preventer repair and replacement",
      "Water softener and filtration installs",
      "Pressure regulation and testing",
    ],
  },
];

export const MARQUEE_ITEMS = [
  "Residential plumbing",
  "Commercial plumbing",
  "Industrial mechanical",
  "Drain cleaning",
  "Hydro excavation",
  "Backflow testing",
  "Industrial welding",
  "24/7 service",
];
