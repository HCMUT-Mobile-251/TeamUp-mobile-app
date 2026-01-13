-- =========================================
-- TeamUp Database - Import All Data
-- =========================================

USE TeamUp;

-- Disable foreign key checks for faster import
SET FOREIGN_KEY_CHECKS = 0;

-- =========================================
-- 1. Import Courses
-- =========================================
SOURCE import_courses.sql;

-- =========================================
-- 2. Import Tags
-- =========================================
SOURCE import_tags.sql;

-- Re-enable foreign key checks
SET FOREIGN_KEY_CHECKS = 1;

-- =========================================
-- Show statistics
-- =========================================
SELECT 'Import completed!' AS Status;
SELECT COUNT(*) AS 'Total Courses' FROM courses;
SELECT COUNT(*) AS 'Total Tags' FROM tags;
