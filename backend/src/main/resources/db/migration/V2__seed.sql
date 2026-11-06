-- Content the site renders. Editable in the database without a redeploy.

INSERT INTO service_offering (slug, name, category, summary, display_order) VALUES
('residential-plumbing', 'Residential plumbing', 'RESIDENTIAL',
 'Repairs, repipes, water heaters, and fixture installs for houses across Central Ohio.', 10),
('commercial-plumbing', 'Commercial plumbing', 'COMMERCIAL',
 'Build-outs, tenant improvements, and scheduled maintenance for schools, offices, and retail.', 20),
('industrial-mechanical', 'Industrial mechanical', 'INDUSTRIAL',
 'Process piping and mechanical systems for manufacturing floors that cannot afford downtime.', 30),
('drain-cleaning', 'Drain cleaning', 'ALL',
 'Cabling, hydro jetting, and camera inspection to find the blockage instead of guessing at it.', 40),
('excavation', 'Excavation & hydro excavation', 'ALL',
 'Water and sewer line replacement, plus vacuum excavation where digging blind is not an option.', 50),
('hvac', 'Heating & cooling', 'ALL',
 'Furnaces, air conditioning, and refrigeration — installed, serviced, and kept running.', 60);

INSERT INTO service_offering_point (service_offering_id, position, point)
SELECT s.id, p.position, p.point FROM service_offering s
JOIN (VALUES
  ('residential-plumbing', 0, 'Water heater replacement and tankless conversion'),
  ('residential-plumbing', 1, 'Whole-house repipe in copper and PEX'),
  ('residential-plumbing', 2, 'Fixture, faucet, and sump pump installs'),
  ('residential-plumbing', 3, 'Frozen and burst line repair'),
  ('commercial-plumbing', 0, 'New construction rough-in and trim'),
  ('commercial-plumbing', 1, 'Backflow testing and certification'),
  ('commercial-plumbing', 2, 'Grease interceptor service'),
  ('commercial-plumbing', 3, 'Planned maintenance agreements'),
  ('industrial-mechanical', 0, 'Process and compressed air piping'),
  ('industrial-mechanical', 1, 'Certified industrial welding'),
  ('industrial-mechanical', 2, 'Equipment tie-ins and relocations'),
  ('industrial-mechanical', 3, 'Shutdown and turnaround support'),
  ('drain-cleaning', 0, 'High-pressure hydro jetting'),
  ('drain-cleaning', 1, 'Sewer camera inspection and locating'),
  ('drain-cleaning', 2, 'Root cutting and descaling'),
  ('drain-cleaning', 3, 'Recurring line maintenance'),
  ('excavation', 0, 'Water and sanitary service replacement'),
  ('excavation', 1, 'Hydro excavation around live utilities'),
  ('excavation', 2, 'Storm and site drainage'),
  ('excavation', 3, 'Trenchless options where the site allows'),
  ('hvac', 0, 'Furnace and A/C replacement'),
  ('hvac', 1, 'Rooftop unit service'),
  ('hvac', 2, 'Commercial refrigeration'),
  ('hvac', 3, 'Seasonal tune-ups')
) AS p(slug, position, point) ON p.slug = s.slug;

INSERT INTO job_posting (title, department, location, employment_type, description) VALUES
('Service Plumber', 'Residential Service', 'Grove City, OH', 'Full-time',
 'Run residential service calls out of a stocked truck. Diagnose, quote, and repair with a dispatcher backing you up.'),
('Commercial Pipefitter', 'Commercial Construction', 'Central Ohio job sites', 'Full-time',
 'Rough-in and trim on school, office, and retail projects. Read prints, lay out from benchmarks, and hit the general''s schedule.'),
('Excavation Operator', 'Excavation', 'Central Ohio job sites', 'Full-time',
 'Run mini-ex and hydro excavation equipment on water and sewer replacements. Dig around live utilities without hitting them.'),
('Apprentice Plumber', 'Apprenticeship', 'Grove City, OH', 'Full-time, paid training',
 'Start with no experience. You work alongside a journeyman, we pay for schooling, and you leave with a card.');

INSERT INTO job_posting_requirement (job_posting_id, position, requirement)
SELECT j.id, r.position, r.requirement FROM job_posting j
JOIN (VALUES
  ('Service Plumber', 0, 'Ohio journeyman card or 4+ years in the trade'),
  ('Service Plumber', 1, 'Clean driving record'),
  ('Service Plumber', 2, 'Comfortable pricing work in front of the customer'),
  ('Commercial Pipefitter', 0, '3+ years commercial rough-in'),
  ('Commercial Pipefitter', 1, 'Print reading and layout'),
  ('Commercial Pipefitter', 2, 'OSHA 10 or willing to get it'),
  ('Excavation Operator', 0, '2+ years operating experience'),
  ('Excavation Operator', 1, 'CDL a plus, not required'),
  ('Excavation Operator', 2, 'Utility locating familiarity'),
  ('Apprentice Plumber', 0, '18 or older with a valid license'),
  ('Apprentice Plumber', 1, 'Show up on time and lift 50 lbs'),
  ('Apprentice Plumber', 2, 'Willing to attend class')
) AS r(title, position, requirement) ON r.title = j.title;
