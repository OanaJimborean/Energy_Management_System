import React from 'react';
import { BrowserRouter as Router, Route, Switch } from 'react-router-dom';
import Login from './components/Login';
import UsersPage from './components/UsersPage';
import GetUsers from './components/GetUsers'; // Import the new component
import { ToastContainer } from 'react-toastify';
import GetDevices from './components/GetDevices';
import UserDevice from './components/UserDevice';
import ChatRoom from './components/ChatRoom';
import 'react-toastify/dist/ReactToastify.css';


function App() {
  return (
    <Router>
      <div className="App">
        <Switch>
          <Route path="/get-users" component={GetUsers} /> {/* New route for GetUsers */}
          <Route path="/userspage" component={UsersPage} />
          <Route path="/get-devices" component={GetDevices}/>
          <Route path="/get-devices-of-users" component={UserDevice}/>
          <Route path="/start-chat" component={ChatRoom}/>
          
          <Route path="/" component={Login} />
        </Switch>
        <ToastContainer position="top-right" autoClose={3000} hideProgressBar />
      </div>
    </Router>
  );
}

export default App;
