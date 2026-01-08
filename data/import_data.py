#!/usr/bin/env python3
"""
Script to import JSON data into MySQL TeamUp database
"""
import json
import mysql.connector
from pathlib import Path

# Database connection
db_config = {
    'host': 'localhost',
    'user': 'root',
    'password': '',  # Empty password as per application.properties
    'database': 'TeamUp'
}

def import_courses():
    """Import courses from mon_hoc.json"""
    print("Importing courses...")
    with open('mon_hoc.json', 'r', encoding='utf-8') as f:
        courses = json.load(f)

    conn = mysql.connector.connect(**db_config)
    cursor = conn.cursor()

    inserted = 0
    for course in courses:
        try:
            cursor.execute(
                "INSERT INTO courses (course_id, name) VALUES (%s, %s) ON DUPLICATE KEY UPDATE name=VALUES(name)",
                (course['courseId'], course['name'])
            )
            inserted += 1
        except Exception as e:
            print(f"Error inserting course {course['courseId']}: {e}")

    conn.commit()
    cursor.close()
    conn.close()
    print(f"✓ Imported {inserted} courses")

def import_tags():
    """Import tags from tag.json"""
    print("Importing tags...")
    with open('tag.json', 'r', encoding='utf-8') as f:
        tags = json.load(f)

    conn = mysql.connector.connect(**db_config)
    cursor = conn.cursor()

    inserted = 0
    for tag in tags:
        try:
            # Generate UUID for tag_id
            cursor.execute(
                "INSERT INTO tags (tag_id, name) VALUES (UUID(), %s) ON DUPLICATE KEY UPDATE name=VALUES(name)",
                (tag['name'],)
            )
            inserted += 1
        except Exception as e:
            print(f"Error inserting tag {tag['name']}: {e}")

    conn.commit()
    cursor.close()
    conn.close()
    print(f"✓ Imported {inserted} tags")

def check_database():
    """Check if database exists and has tables"""
    try:
        conn = mysql.connector.connect(**db_config)
        cursor = conn.cursor()

        # Check tables
        cursor.execute("SHOW TABLES")
        tables = cursor.fetchall()

        print(f"\nDatabase: {db_config['database']}")
        print(f"Tables found: {len(tables)}")
        for table in tables:
            print(f"  - {table[0]}")

        cursor.close()
        conn.close()
        return True
    except mysql.connector.Error as e:
        print(f"Database error: {e}")
        return False

def main():
    print("=" * 50)
    print("TeamUp Database Data Import")
    print("=" * 50)

    # Change to data directory
    script_dir = Path(__file__).parent
    import os
    os.chdir(script_dir)

    # Check database
    if not check_database():
        print("\n❌ Cannot connect to database!")
        print("Make sure MySQL is running and database 'TeamUp' exists.")
        return

    print("\n" + "=" * 50)
    print("Starting import...")
    print("=" * 50 + "\n")

    # Import data
    import_courses()
    import_tags()

    print("\n" + "=" * 50)
    print("Import completed!")
    print("=" * 50)

    # Show statistics
    conn = mysql.connector.connect(**db_config)
    cursor = conn.cursor()

    cursor.execute("SELECT COUNT(*) FROM courses")
    course_count = cursor.fetchone()[0]

    cursor.execute("SELECT COUNT(*) FROM tags")
    tag_count = cursor.fetchone()[0]

    print(f"\nDatabase statistics:")
    print(f"  Courses: {course_count}")
    print(f"  Tags: {tag_count}")

    cursor.close()
    conn.close()

if __name__ == "__main__":
    main()
