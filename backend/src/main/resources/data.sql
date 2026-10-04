INSERT INTO company (id, name, city) VALUES
  (1, 'Rheinblick Immobilien GmbH', 'Bonn'),
  (2, 'UrbanNest Property AG', 'Köln');

INSERT INTO real_estate_property (id, company_id, name, address, city, type, area, market_value, status, image_path) VALUES
  (1, 1, 'Rheinblick Offices', 'Friedrichstraße 12', 'Bonn', 'Office', 2450.00, 6850000.00, 'Leased', 'reports/images/rheinblick-offices.svg'),
  (2, 1, 'Villa am Park', 'Rheinaustraße 31', 'Bonn', 'Residential', 420.00, 2290000.00, 'Available', 'reports/images/villa-am-park.svg'),
  (3, 1, 'Logistikzentrum Nord', 'Maarstraße 88', 'Troisdorf', 'Logistics', 5120.00, 4920000.00, 'Leased', 'reports/images/logistikzentrum-nord.svg'),
  (4, 1, 'Rheinblick Offices', 'Friedrichs‚traße 12', 'Bonn', 'Office', 2450.00, 6850000.00, 'Leased', 'reports/images/rheinblick-offices.svg'),
  (5, 1, 'Villa am Park', 'Rheinaustraße 31', 'Bonn', 'Residential', 420.00, 2290000.00, 'Available', 'reports/images/villa-am-park.svg'),
  (6, 1, 'Logistikzentrum Nord', 'Maarstraße 88', 'Troisdorf', 'Logistics', 5120.00, 4920000.00, 'Leased', 'reports/images/logistikzentrum-nord.svg'),
  (7, 1, 'Rheinblick Offices', 'Friedrichstraße 12', 'Bonn', 'Office', 2450.00, 6850000.00, 'Leased', 'reports/images/rheinblick-offices.svg'),
  (8, 1, 'Villa am Park', 'Rheinaustraße 31', 'Bonn', 'Residential', 420.00, 2290000.00, 'Available', 'reports/images/villa-am-park.svg'),
  (9, 1, 'Logistikzentrum Nord', 'Maarstraße 88', 'Troisdorf', 'Logistics', 5120.00, 4920000.00, 'Leased', 'reports/images/logistikzentrum-nord.svg'),
  (10, 1, 'Rheinblick Offices', 'Friedrichstraße 12', 'Bonn', 'Office', 2450.00, 6850000.00, 'Leased', 'reports/images/rheinblick-offices.svg'),
  (11, 1, 'Villa am Park', 'Rheinaustraße 31', 'Bonn', 'Residential', 420.00, 2290000.00, 'Available', 'reports/images/villa-am-park.svg'),
  (12, 1, 'Logistikzentrum Nord', 'Maarstraße 88', 'Troisdorf', 'Logistics', 5120.00, 4920000.00, 'Leased', 'reports/images/logistikzentrum-nord.svg'),
  (13, 2, 'Quartier Ehrenfeld', 'Venloer Straße 214', 'Köln', 'Residential', 3180.00, 12400000.00, 'Development', 'reports/images/quartier-ehrenfeld.svg'),
  (14, 2, 'Media Campus', 'Schanzenstraße 45', 'Köln', 'Office', 4760.00, 15350000.00, 'Leased', 'reports/images/media-campus.svg'),
  (15, 2, 'Retail Arcades', 'Aachener Straße 126', 'Köln', 'Retail', 1910.00, 8975000.00, 'Available', 'reports/images/retail-arcades.svg'),
  (16, 2, 'Quartier Ehrenfeld', 'Venloer Straße 214', 'Köln', 'Residential', 3180.00, 12400000.00, 'Development', 'reports/images/quartier-ehrenfeld.svg'),
  (17, 2, 'Media Campus', 'Schanzenstraße 45', 'Köln', 'Office', 4760.00, 15350000.00, 'Leased', 'reports/images/media-campus.svg'),
  (18, 2, 'Retail Arcades', 'Aachener Straße 126', 'Köln', 'Retail', 1910.00, 8975000.00, 'Available', 'reports/images/retail-arcades.svg'),
  (19, 2, 'Quartier Ehrenfeld', 'Venloer Straße 214', 'Köln', 'Residential', 3180.00, 12400000.00, 'Development', 'reports/images/quartier-ehrenfeld.svg'),
  (20, 2, 'Media Campus', 'Schanzenstraße 45', 'Köln', 'Office', 4760.00, 15350000.00, 'Leased', 'reports/images/media-campus.svg'),
  (21, 2, 'Retail Arcades', 'Aachener Straße 126', 'Köln', 'Retail', 1910.00, 8975000.00, 'Available', 'reports/images/retail-arcades.svg'),
  (22, 2, 'Quartier Ehrenfeld', 'Venloer Straße 214', 'Köln', 'Residential', 3180.00, 12400000.00, 'Development', 'reports/images/quartier-ehrenfeld.svg'),
  (23, 2, 'Media Campus', 'Schanzenstraße 45', 'Köln', 'Office', 4760.00, 15350000.00, 'Leased', 'reports/images/media-campus.svg'),
  (24, 2, 'Retail Arcades', 'Aachener Straße 126', 'Köln', 'Retail', 1910.00, 8975000.00, 'Available', 'reports/images/retail-arcades.svg');


ALTER TABLE company ALTER COLUMN id RESTART WITH 3;
ALTER TABLE real_estate_property ALTER COLUMN id RESTART WITH 7;
