import React, { useState } from 'react';
import { useHistory } from 'react-router-dom';
import "../index.css";

const Login = () => {
  const history = useHistory();
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');


  const handleLogin = () => {
    // Construct the request body
    const requestBody = JSON.stringify({
      email: email,
      password: password,
    });

    // Make a POST request to your backend login endpoint
    fetch('http://localhost:8082/user/login', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: requestBody,
    })
      .then(response => response.json())
      .then(data => {console.log(data);
        if (data.token) {
          
          localStorage.setItem('token', data.token);
          alert('User logged in successfully');
          history.push('/userspage');
          history.push({
            pathname: '/userspage',
            state: { email: email } // Pass email to the UsersPage
          });
        } else {
          
          alert('Login failed. Please check your credentials.');
        }
      })
      .catch(error => {
        console.error('Error:', error);
      });
  };

  return (
    <div className="login-container">
      <h1>Welcome to Energy-Management System App</h1>
      <form >
        <div className="form-group">
          <label htmlFor="email">Email</label>
          <input type="text" id="email" name="email" value={email} onChange={(e) => setEmail(e.target.value)} />
        </div>
        <div className="form-group">
          <label htmlFor="password">Password</label>
          <input type="password" id="password" name="password" value={password} onChange={(e) => setPassword(e.target.value)} />
        </div>
        <button className="login-button" type="button" onClick={handleLogin}>Login</button>
      </form>
    </div>
  );
};

export default Login;
