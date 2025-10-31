/* 
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Other/SQLTemplate.sql to edit this template
 */
/**
 * Author:  User
 * Created: 24 Oct 2025
 */

-- ClubConnect Database Schema
CREATE DATABASE IF NOT EXISTS clubconnect_db;
USE clubconnect_db;

CREATE TABLE IF NOT EXISTS users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(120) NOT NULL,
    email VARCHAR(190) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role ENUM('Admin','Leader','Member','Guest') DEFAULT 'Guest',
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);


-- Clubs Table
CREATE TABLE IF NOT EXISTS clubs (
    club_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(150) NOT NULL UNIQUE,
    description TEXT,
    status ENUM('Pending','Approved','Rejected') DEFAULT 'Pending',
    leader_id INT,
    FOREIGN KEY (leader_id) REFERENCES users(user_id)
);

-- Memberships Table
CREATE TABLE IF NOT EXISTS memberships (
    membership_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    club_id INT NOT NULL,
    status ENUM('Pending','Active','Alumni','Rejected') DEFAULT 'Pending',
    joined_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id),
    FOREIGN KEY (club_id) REFERENCES clubs(club_id)
);

-- Rooms Table
CREATE TABLE IF NOT EXISTS rooms (
    room_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    capacity INT DEFAULT 30
);

-- Events Table
CREATE TABLE IF NOT EXISTS events (
    event_id INT AUTO_INCREMENT PRIMARY KEY,
    club_id INT NOT NULL,
    name VARCHAR(150) NOT NULL,
    description TEXT,
    event_date DATETIME NOT NULL,
    room_id INT,
    capacity INT DEFAULT 50,
    FOREIGN KEY (club_id) REFERENCES clubs(club_id),
    FOREIGN KEY (room_id) REFERENCES rooms(room_id)
);

-- Attendance Table
CREATE TABLE IF NOT EXISTS attendance (
    attendance_id INT AUTO_INCREMENT PRIMARY KEY,
    event_id INT NOT NULL,
    user_id INT NOT NULL,
	check_in_time DATETIME DEFAULT NULL,
    status ENUM('Present','Absent') DEFAULT 'Absent',
    FOREIGN KEY (event_id) REFERENCES events(event_id),
    FOREIGN KEY (user_id) REFERENCES users(user_id)
);

-- Budget Requests Table
CREATE TABLE IF NOT EXISTS budget_requests (
    budget_id INT AUTO_INCREMENT PRIMARY KEY,
    event_id INT NOT NULL,
    amount DECIMAL(10,2) NOT NULL,
    status ENUM('Pending','Approved','Rejected') DEFAULT 'Pending',
    purpose VARCHAR(255),
    FOREIGN KEY (event_id) REFERENCES events(event_id)
);

CREATE TABLE IF NOT EXISTS notifications (
    notification_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    club_id INT,
    message TEXT NOT NULL,
    sent_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id),
    FOREIGN KEY (club_id) REFERENCES clubs(club_id)
);
CREATE TABLE IF NOT EXISTS event_rsvps (
    rsvp_id INT AUTO_INCREMENT PRIMARY KEY,
    event_id INT NOT NULL,
    user_id INT NOT NULL,
    rsvp_status ENUM('Yes','No','Maybe','Waitlist') DEFAULT 'No',
    waitlist_position INT DEFAULT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (event_id) REFERENCES events(event_id),
    FOREIGN KEY (user_id) REFERENCES users(user_id),
    UNIQUE(event_id, user_id)
);
CREATE TABLE IF NOT EXISTS event_waitlist_notifications (
    notification_id INT AUTO_INCREMENT PRIMARY KEY,
    rsvp_id INT NOT NULL,
    notified_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (rsvp_id) REFERENCES event_rsvps(rsvp_id)
	);