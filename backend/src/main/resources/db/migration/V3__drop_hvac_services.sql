-- The site no longer offers heating and cooling work.
-- V2 is left untouched: it has already run against existing databases, and
-- editing an applied migration breaks Flyway's checksum on the next boot.

DELETE FROM service_offering WHERE slug = 'hvac';
-- service_offering_point rows cascade on delete.

INSERT INTO service_offering (slug, name, category, summary, display_order) VALUES
('backflow-testing', 'Backflow & water quality', 'ALL',
 'Certification testing, repairs, and treatment so what comes out of the tap is what should.', 60);

INSERT INTO service_offering_point (service_offering_id, position, point)
SELECT s.id, p.position, p.point FROM service_offering s
JOIN (VALUES
  ('backflow-testing', 0, 'Annual backflow testing and certification'),
  ('backflow-testing', 1, 'Preventer repair and replacement'),
  ('backflow-testing', 2, 'Water softener and filtration installs'),
  ('backflow-testing', 3, 'Pressure regulation and testing')
) AS p(slug, position, point) ON p.slug = s.slug;
