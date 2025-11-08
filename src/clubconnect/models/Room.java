/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package clubconnect.models;

public class Room {
    private int roomId;
    private String roomName;
    private int capacity;
    private String status; // store status too

    public Room(int roomId, String roomName, int capacity, String status) {
        this.roomId = roomId;
        this.roomName = roomName;
        this.capacity = capacity;
        this.status = status;
    }

    public int getRoomId() { return roomId; }
    public String getRoomName() { return roomName; }
    public int getCapacity() { return capacity; }
    public String getStatus() { return status; }

    @Override
    public String toString() { 
        return roomName + " (" + capacity + ")"; 
    }
}
