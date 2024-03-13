import React, { useState, useEffect } from 'react';
import '../users-page.css';
import { toast } from 'react-toastify';

const GetUsers = () => {
    const [users, setUsers] = useState([]);
    const [message, setMessage] = useState(null);
    const [editedUser, setEditedUser] = useState(null);
    const [addingUser, setAddingUser] = useState(false); 
    const [newUser, setNewUser] = useState({
        username: '',
        email: '',
        password: '',
        role: ''
    });

    

    useEffect(() => {
        // Fetch the list of users when the component mounts
        fetchUsers();
    }, []);

    const fetchUsers = () => {
        // Fetch the list of users from your backend
        const token = localStorage.getItem('token');
        fetch('http://localhost:8082/user/getUsers', {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${token}`,
            },
        })
            .then((response) => {
                if (response.status === 200) {
                    return response.json();
                }
                if (response.status === 401) {
                    alert("Unauthorized Access")
                }
                else {
                    throw new Error('Failed to fetch data');
                }
            })
            .then((data) => {
                setUsers(data);
            })
            .catch((error) => {
                console.error('Error:', error);
            });
    };

    const handleEdit = (user) => {
        setEditedUser(user);
    };

    const handleCancelEdit = () => {
        setEditedUser(null);
    };

    const handleUpdate = () => {
        const token = localStorage.getItem('token');
        fetch('http://localhost:8082/user/update', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${token}`,
            },
            body: JSON.stringify({
                username: editedUser.username,
                email: editedUser.email,
                password: editedUser.password,
                role: editedUser.role,
                userId: editedUser.userId,
            }),
        })
            .then((response) => {
                if (response.status === 200) {
                    toast.success('User updated successfully.', {
                        style: { background: '#90d490' },
                        bodyClassName: 'custom-toast-content',
                    });
                    setEditedUser(null);
                    fetchUsers();
                }
                else {
                    toast.error('Failed to update user.', {
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

    const handleDelete = (userId) => {
        const token = localStorage.getItem('token');
        
        // Delete the user first
        fetch(`http://localhost:8082/user/delete/${userId}`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${token}`,
            },
        })
        .then((userResponse) => {
            if (userResponse.status === 200) {
                toast.success('User deleted successfully. No device assigned', {
                    style: { background: '#90d490' },
                    bodyClassName: 'custom-toast-content',
                });
                fetchUsers();
                // User deleted successfully, now delete the associated devices
                return fetch(`http://localhost:8081/device/deleteByUserId/${userId}`, {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                        'Authorization': `Bearer ${token}`,
                    },
                });
            } else {
                throw new Error('Failed to delete user');
            }
        })
        .then((deviceResponse) => {
            if (deviceResponse && deviceResponse.status === 200) {
                toast.success('Associated device deleted successfully.', {
                    style: { background: '#90d490' },
                    bodyClassName: 'custom-toast-content',
                });
                fetchUsers(); // Refresh the user list
            // } else if (deviceResponse) {
            //     toast.error('No device assignet.', {
            //         style: { background: 'red' },
            //     });
            // }
    }})
        .catch((error) => {
            showMessage('An error occurred');
        });
    };
    


    const handleAddUser = () => {
        setAddingUser(true);
    };
    
    const handleSaveUser = () => {
    
        const token = localStorage.getItem('token');
        const newUserData = {
            username: newUser.username,
            email: newUser.email,
            password: newUser.password,
            role: newUser.role,
        };

        fetch('http://localhost:8082/user/signup', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${token}`,
            },
            body: JSON.stringify(newUserData),
        })
            .then((response) => {
                if (response.status === 200) { 
                    toast.success('User added successfully.', {
                        style: { background: '#90d490' },
                        bodyClassName: 'custom-toast-content',
                    });
                    fetchUsers();
                    setAddingUser(false); // Close the form
                    setNewUser({
                        username: '',
                        email: '',
                        password: '',
                        role: '',
                    });
                } else {
                    toast.error('Failed to add user.', {
                        style: { background: 'red' },
                    });
                }
            })
            .catch((error) => {
                showMessage('An error occurred');
            });
        };

    return (
        <div>
            <h2>List of Users</h2>
            {message && <p className="message">{message}</p>}
            <button className="edit-delete-buttons" onClick={handleAddUser}>
                Add User
            </button>
            {addingUser && (
                <div className="user-card">
                <h3>Add New User:</h3>
                <label>UserName:</label>
            <input
                type="text"
                value={newUser.username}
                onChange={(e) =>
                    setNewUser({
                        ...newUser,
                        username: e.target.value,
                    })
                }
            />
            <label>Email:</label>
            <input
                type="text"
                value={newUser.email}
                onChange={(e) =>
                    setNewUser({
                        ...newUser,
                        email: e.target.value,
                    })
                }
            />
            <label>Password:</label>
            <input
                type="text"
                value={newUser.password}
                onChange={(e) =>
                    setNewUser({
                        ...newUser,
                        password: e.target.value,
                    })
                }
            />
            <label>Role:</label>
            <input
                type="text"
                value={newUser.role}
                onChange={(e) =>
                    setNewUser({
                        ...newUser,
                        role: e.target.value,
                    })
                }
            />
            <button
                        className="edit-delete-buttons"
                        type="button"
                        onClick={handleSaveUser}
                    >
                        Save User
                    </button>
                </div>
            )}
            <div className="user-columns">
                {users && users.map((user) => (
                    <div className="user-card" key={user.userId}>
                        <h3>User: </h3>
                        {editedUser && editedUser.userId === user.userId ? (
                            <div>
                                <label>Username:</label>
                                <input
                                    type="text"
                                    value={editedUser.username}
                                    onChange={(e) =>
                                        setEditedUser({ ...editedUser, username: e.target.value })
                                    }
                                />
                                <label>Email:</label>
                                <input
                                    type="text"
                                    value={editedUser.email}
                                    onChange={(e) =>
                                        setEditedUser({ ...editedUser, email: e.target.value })
                                    }
                                />
                                <label>Role:</label>
                                <input
                                    type="text"
                                    value={editedUser.role}
                                    onChange={(e) =>
                                        setEditedUser({ ...editedUser, role: e.target.value })
                                    }
                                />
                                <label>Id:</label>
                                <input
                                    type="text"
                                    value={editedUser.userId}
                                    onChange={(e) =>
                                        setEditedUser({ ...editedUser, userId: e.target.value })
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
                                <p>Username: {user.username}</p>
                                <p>Email: {user.email}</p>
                                <p>Role: {user.role}</p>
                                <p>Id: {user.userId}</p>
                                <div>
                                    <button
                                        className="edit-delete-buttons"
                                        type="button"
                                        onClick={() => handleEdit(user)}
                                    >
                                        Edit
                                    </button>
                                    <button
                                        className="edit-delete-buttons"
                                        type="button"
                                        onClick={() => handleDelete(user.userId)}
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

export default GetUsers;
