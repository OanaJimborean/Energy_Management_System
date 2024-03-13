import React from 'react';
import { Link } from 'react-router-dom';
import "../index.css";

const UsersPage = ({location}) => {
  const userEmail = location.state?.email || ''; 
  
  return (
    <div className="page-container">
      <h2 className="page-title">Welcome to the User Management Dashboard</h2>
      <div className="link-container">
        <Link to="/get-users">See All Users</Link>
      </div>
      <div className="link-container">
        <Link to="/get-devices">See All Devices</Link>
      </div>
      <div className="link-container">
        <Link to="/get-devices-of-users">See My Devices</Link>
      </div>
      <div className="link-container">
        <Link to={{
            pathname: '/start-chat',
            state: { email: userEmail } }}>Start Chat</Link>
      </div>
    </div>
  );
};

export default UsersPage;
