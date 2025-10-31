/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package clubconnect.models;

public class Room {
    private int roomId;
    private String RoomName;
    private int capacity;

    public Room(int roomId, String RoomName, int capacity) {
        this.roomId = roomId;
        this.RoomName = RoomName;
        this.capacity = capacity;
    }

    public Room(String name, int capacity) {
        this(0, name, capacity);
    }

    public int getRoomId() { return roomId; }
    public void setRoomId(int roomId) { this.roomId = roomId; }

    public String getRoomName() { return RoomName; }
    public void setName(String RoomName) { this.RoomName = RoomName; }

    public int getCapacity() { return capacity; }
    public void setCapacity(int capacity) { this.capacity = capacity; }

    @Override
    public String toString() { return RoomName + " (" + capacity + ")"; }
}

