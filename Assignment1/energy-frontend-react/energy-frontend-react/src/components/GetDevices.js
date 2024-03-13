import React, { useState, useEffect } from 'react';
import '../users-page.css'; // You can create a CSS file for device styles
import { toast } from 'react-toastify';

const GetDevices = () => {
    const [devices, setDevices] = useState([]);
    const [message, setMessage] = useState(null);
    const [editedDevice, setEditedDevice] = useState(null);
    const [addingDevice, setAddingDevice] = useState(false); // State variable to control adding a new device
    const [newDevice, setNewDevice] = useState({
        device_name: '',
        address: '',
        description: '',
        max_hourly: '',
        userId: '',
    });

    useEffect(() => {
        // Fetch the list of devices when the component mounts
        fetchDevices();
    }, []);

    const fetchDevices = () => {
        // Fetch the list of devices from your backend
        const token = localStorage.getItem('token');
        const decodedToken = JSON.parse(atob(token.split('.')[1])); // Decodes the base64 token payload
        const userRole = decodedToken.role;


        fetch('http://localhost:8081/device/getDevices', {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${token}`,
            },
        })
            .then((response) => {
                if (userRole !== 'admin') {
                    alert("Unauthorized Access");
                    return;
                }
                if (response.status === 200) {
                    return response.json();
                } else if (response.status === 401) {
                    alert('Unauthorized Access');
                } else {
                    throw new Error('Failed to fetch data');
                }
            })
            .then((data) => {
                setDevices(data);
            })
            .catch((error) => {
                console.error('Error:', error);
            });
    };

    const handleEdit = (device) => {
        setEditedDevice(device);
    };

    const handleCancelEdit = () => {
        setEditedDevice(null);
    };

    const handleUpdate = () => {
        const token = localStorage.getItem('token');

        fetch('http://localhost:8081/device/update', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${token}`,
            },
            body: JSON.stringify({
                deviceId: editedDevice.deviceId,
                device_name: editedDevice.device_name,
                address: editedDevice.address,
                description: editedDevice.description,
                max_hourly: editedDevice.max_hourly,
                userId: editedDevice.userId,
            }),
        })
            .then((response) => {
                if (response.status === 200) {
                    toast.success('Device updated successfully.', {
                        style: { background: '#90d490' },
                        bodyClassName: 'custom-toast-content',
                    });
                    setEditedDevice(null);
                    fetchDevices();
                } else {
                    toast.error('Failed to update device.', {
                        style: { background: 'red' },
                    });
                }
            })
            .catch((error) => {
                showMessage('An error occurred');
            });
    };

    const showMessage = (text) => {
        setMessage(text);

        setTimeout(() => {
            setMessage(null);
        }, 2000);
    };

    const handleDelete = (deviceId) => {
        const token = localStorage.getItem('token');
        fetch(`http://localhost:8081/device/delete/${deviceId}`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${token}`,
            },
        })
            .then((response) => {
                if (response.status === 200) {
                    toast.success('Device deleted successfully.', {
                        style: { background: '#90d490' },
                        bodyClassName: 'custom-toast-content',
                    });
                    fetchDevices();
                } else {
                    toast.error('Failed to delete device.', {
                        style: { background: 'red' },
                    });
                }
            })
            .catch((error) => {
                showMessage('An error occurred');
            });
    };

    const handleAddDevice = () => {
        setAddingDevice(true);
    };

    const handleSaveDevice = () => {
    
        const token = localStorage.getItem('token');
        const newDeviceData = {
            device_name: newDevice.device_name,
            address: newDevice.address,
            description: newDevice.description,
            max_hourly: newDevice.max_hourly,
            userId: newDevice.userId,
        };
    
    
        fetch(`http://localhost:8082/user/checkUserExistence/${newDevice.userId}`, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${token}`,
            },
        })
            .then((response) => {
                if (response.status === 200) {
                    return fetch('http://localhost:8081/device/add', {
                        method: 'POST',
                        headers: {
                            'Content-Type': 'application/json',
                            'Authorization': `Bearer ${token}`,
                        },
                        body: JSON.stringify(newDeviceData),
                    });
                } else {
                    toast.error('Failed to add device.', {
                        style: { background: 'red' },
                    });
                    return null;
                }
            })
            .then((response) => {
                if (response && response.status === 200) {
                    toast.success('Device added successfully.', {
                        style: { background: '#90d490' },
                        bodyClassName: 'custom-toast-content',
                    });
                    fetchDevices(); 
                    setAddingDevice(false);
                    setNewDevice({
                    device_name: '',
                    address: '',
                    description: '',
                    max_hourly: '',
                    userId: '',
                });
                } else if (response) {
                    toast.error('Failed to add device. This UserId does not exist.', {
                        style: { background: 'red' },
                        body: JSON.stringify(newDeviceData),
                    });
                    
                }
            })
            .catch((error) => {
                showMessage('An error occurred');
            });
    };
    

                return (
                    <div>
                        <h2>List of Devices</h2>
                        {message && <p className="message">{message}</p>}
                        <button className="edit-delete-buttons" onClick={handleAddDevice}>
                            Add Device
                        </button>
                        {addingDevice && (
                            <div className="user-card">
                                <h3>Add New Device:</h3>
                                <label>Device Name:</label>
                                <input
                                    type="text"
                                    value={newDevice.device_name}
                                    onChange={(e) =>
                                        setNewDevice({
                                            ...newDevice,
                                            device_name: e.target.value,
                                        })
                                    }
                                />
                                <label>Address:</label>
                                <input
                                    type="text"
                                    value={newDevice.address}
                                    onChange={(e) =>
                                        setNewDevice({
                                            ...newDevice,
                                            address: e.target.value,
                                        })
                                    }
                                />
                                <label>Description:</label>
                                <input
                                    type="text"
                                    value={newDevice.description}
                                    onChange={(e) =>
                                        setNewDevice({
                                            ...newDevice,
                                            description: e.target.value,
                                        })
                                    }
                                />
                                <label>Max Hourly:</label>
                                <input
                                    type="text"
                                    value={newDevice.max_hourly}
                                    onChange={(e) =>
                                        setNewDevice({
                                            ...newDevice,
                                            max_hourly: e.target.value,
                                        })
                                    }
                                />
                                <label>User Id:</label>
                                <input
                                    type="text"
                                    value={newDevice.userId}
                                    onChange={(e) =>
                                        setNewDevice({
                                            ...newDevice,
                                            userId: e.target.value,
                                        })
                                    }
                                />
                                <button
                                    className="edit-delete-buttons"
                                    type="button"
                                    onClick={handleSaveDevice}
                                >
                                    Save Device
                                </button>
                            </div>
                        )}
                        <div className="user-columns">
                            {devices &&
                                devices.map((device) => (
                                    <div className="user-card" key={device.deviceId}>
                                        <h3>Device:</h3>
                                        {editedDevice && editedDevice.deviceId === device.deviceId ? (
                                            <div>
                                                <label>Id:</label>
                                                <input
                                                    type="text"
                                                    value={editedDevice.deviceId}
                                                    onChange={(e) =>
                                                        setEditedDevice({
                                                            ...editedDevice,
                                                            deviceId: e.target.value,
                                                        })
                                                    }
                                                />
                                                <label>Device Name:</label>
                                                <input
                                                    type="text"
                                                    value={editedDevice.device_name}
                                                    onChange={(e) =>
                                                        setEditedDevice({
                                                            ...editedDevice,
                                                            device_name: e.target.value,
                                                        })
                                                    }
                                                />
                                                <label>Address:</label>
                                                <input
                                                    type="text"
                                                    value={editedDevice.address}
                                                    onChange={(e) =>
                                                        setEditedDevice({
                                                            ...editedDevice,
                                                            address: e.target.value,
                                                        })
                                                    }
                                                />
                                                <label>Description:</label>
                                                <input
                                                    type="text"
                                                    value={editedDevice.description}
                                                    onChange={(e) =>
                                                        setEditedDevice({
                                                            ...editedDevice,
                                                            description: e.target.value,
                                                        })
                                                    }
                                                />
                                                <label>Max Hourly:</label>
                                                <input
                                                    type="text"
                                                    value={editedDevice.max_hourly}
                                                    onChange={(e) =>
                                                        setEditedDevice({
                                                            ...editedDevice,
                                                            max_hourly: e.target.value,
                                                        })
                                                    }
                                                />

                                                <label>User Id:</label>
                                                <input
                                                    type="text"
                                                    value={editedDevice.userId}
                                                    onChange={(e) =>
                                                        setEditedDevice({
                                                            ...editedDevice,
                                                            userId: e.target.value,
                                                        })
                                                    }
                                                />
                                                <button
                                                    className="edit-delete-buttons"
                                                    type="button"
                                                    onClick={handleUpdate}
                                                >
                                                    Save
                                                </button>
                                                <button
                                                    className="edit-delete-buttons"
                                                    type="button"
                                                    onClick={handleCancelEdit}
                                                >
                                                    Cancel
                                                </button>
                                            </div>
                                        ) : (
                                            <div>
                                                <p>Id: {device.deviceId}</p>
                                                <p>Device Name: {device.device_name}</p>
                                                <p>Address: {device.address}</p>
                                                <p>Description: {device.description}</p>
                                                <p>Max Hourly: {device.max_hourly}</p>
                                                <p>User Id: {device.userId}</p>

                                                <div>
                                                    <button
                                                        className="edit-delete-buttons"
                                                        type="button"
                                                        onClick={() => handleEdit(device)}
                                                    >
                                                        Edit
                                                    </button>
                                                    <button
                                                        className="edit-delete-buttons"
                                                        type="button"
                                                        onClick={() => handleDelete(device.deviceId)}
                                                    >
                                                        Delete
                                                    </button>
                                                </div>
                                            </div>
                                        )}
                                    </div>
                                ))}
                        </div>
                    </div>
                );
            };

        export default GetDevices;
