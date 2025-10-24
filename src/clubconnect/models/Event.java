/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package clubconnect.models;

import java.util.Date;

public class Event {
    private int eventId;
    private int clubId;
    private String name;
    private String description;
    private Date eventDate;
    private int roomId;
    private int capacity;

    // Constructor
    public Event(int eventId, int clubId, String name, String description, Date eventDate, int roomId, int capacity) {
        this.eventId = eventId;
        this.clubId = clubId;
        this.name = name;
        this.description = description;
        this.eventDate = eventDate;
        this.roomId = roomId;
        this.capacity = capacity;
    }

    // Getters/setters
    public int getEventId() { return eventId; }
    public void setEventId(int eventId) { this.eventId = eventId; }

    public int getClubId() { return clubId; }
    public void setClubId(int clubId) { this.clubId = clubId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Date getEventDate() { return eventDate; }
    public void setEventDate(Date eventDate) { this.eventDate = eventDate; }

    public int getRoomId() { return roomId; }
    public void setRoomId(int roomId) { this.roomId = roomId; }

    public int getCapacity() { return capacity; }
    public void setCapacity(int capacity) { this.capacity = capacity; }
}

