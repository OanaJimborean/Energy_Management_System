import React, { useState, useEffect } from 'react';
import '../users-page.css';

const UserDevice = () => {
    const [devices, setDevices] = useState([]);
    const [isLoading, setIsLoading] = useState(true);

    useEffect(() => {
        // Fetch the devices assigned to the logged-in user when the component mounts
        fetchDevicesByUserId();
    }, []);

    const fetchDevicesByUserId = (userId) => {
        // Make an API request to your backend to fetch devices by user ID
        const token = localStorage.getItem('token');
        const decodedToken = JSON.parse(atob(token.split('.')[1]));
        userId = parseInt(decodedToken.userId);
        fetch(`http://localhost:8081/device/getDevicesByUserId/${userId}`, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${token}`,
            },
        })
        .then((response) => {
            if (response.status === 200) {
                return response.json();
            } else {
                throw new Error('Failed to fetch devices by user ID');
            }
        })
        .then((data) => {
            setDevices(data);
            setIsLoading(false);
        })
        .catch((error) => {
            console.error('Error:', error);
        });
    };

    return (
        <div className="user-device-container">
            <h2>Your Assigned Devices</h2>
            {isLoading ? (
                <p>Loading...</p>
            ) : (
                <ul className="device-list">
                    {devices.map((device) => (
                        <li key={device.deviceId} className="device-item">
                            <strong>Device Name:</strong> {device.device_name}
                            <br />
                            <strong>Address:</strong> {device.address}
                            <br />
                            <strong>Description:</strong> {device.description}
                            <br />
                            <strong>Max Hourly:</strong> {device.max_hourly}
                            <br />
                            <strong>User Id:</strong> {device.userId}
                        </li>
                    ))}
                </ul>
            )}
        </div>
    );
};

export default UserDevice;
