import React, { useEffect, useState } from 'react';
import { over } from 'stompjs';
import SockJS from 'sockjs-client';
import '../chat.css';
import { useLocation } from 'react-router-dom';

var stompClient = null;

const ChatRoom = () => {
  const location = useLocation();
  const userEmail = location.state?.email || '';
  const [privateChats, setPrivateChats] = useState(new Map());
  const [publicChats, setPublicChats] = useState([]);
  const [tab, setTab] = useState('CHATROOM');
  const [loggedInUsers, setLoggedInUsers] = useState([]);
  const [typingStatus, setTypingStatus] = useState('');
  const [message, setMessage] = useState('');
  const [receiverFocus, setReceiverFocus] = useState(false);
  const [seenMessages, setSeenMessages] = useState(new Set());

  // Function to send notification to the sender when the receiver focuses on the input field
const connect = () => {
    let Sock = new SockJS('http://localhost:8084/ws');
    stompClient = over(Sock);
    stompClient.connect({}, onConnected, onError);
  };
  
  

  const onConnected = () => {
    if (stompClient && stompClient.connected){
      setUserData({ ...userData, connected: true });

    //try {
      stompClient.subscribe('/chatroom/public', onMessageReceived);
      stompClient.subscribe('/user/' + userData.username + '/private', onPrivateMessage);
      stompClient.subscribe('/topic/typing', onTypingReceived);
      userJoin();
    //} catch (error) {}
  }
  };
  


  useEffect(() => {
    connect();
    console.log(userData);
    //fetchLoggedInUsers();
  }, []);


  
  const userJoin = () => {
    var chatMessage = {
      senderName: userData.username,
      status: 'JOIN',
    };
    stompClient.send('/app/message', {}, JSON.stringify(chatMessage));
  };

  const onMessageReceived = (payload) => {
    var payloadData = JSON.parse(payload.body);
    switch (payloadData.status) {
      case "JOIN":
        if (!privateChats.get(payloadData.senderName)) {
          privateChats.set(payloadData.senderName, []);
          setLoggedInUsers((prevUsers) => [...prevUsers, payloadData.senderName]);
        }
        break;
      case "MESSAGE":
        publicChats.push(payloadData);
        setPublicChats([...publicChats]);
        break;
      default:
        console.log("Unexpected status:", payloadData.status);
        break;
    }
  };
  


  const onPrivateMessage = (payload) => {
    var payloadData = JSON.parse(payload.body);
    const senderChats = privateChats.get(payloadData.senderName) || [];
    const updatedChats = [...senderChats, payloadData];
    setPrivateChats(new Map(privateChats.set(payloadData.senderName, updatedChats)));
    setTypingStatus('');
  };  
  
  const onError = (err) => {
    console.log('WebSocket error:', err);
  };
  

  const handleMessage = (event) => {
    const { value } = event.target;
    setUserData({ ...userData, message: value });
  };


  const sendValue = () => {
    if (stompClient && stompClient.connected) {
      var chatMessage = {
        senderName: userData.username,
        message: userData.message,
        status: 'MESSAGE',
      };
      console.log(chatMessage);
      stompClient.send('/app/message', {}, JSON.stringify(chatMessage));
      setUserData({ ...userData, message: '' });
      setTypingStatus('');
    } else {
      console.error('WebSocket connection is not established.');
    }
  };
  
  const sendPrivateValue = () => {
    if (stompClient && stompClient.connected && userData.username !== tab) {
      var chatMessage = {
        senderName: userData.username,
        receiverName: tab,
        message: userData.message,
        status: 'MESSAGE',
      };
  
      if (userData.username !== tab) {
        privateChats.get(tab).push(chatMessage);
        setPrivateChats(new Map(privateChats));
      }
      stompClient.send('/app/private-message', {}, JSON.stringify(chatMessage));
      setUserData({ ...userData, message: '' });
      setTypingStatus('');
    } else {
      console.error('WebSocket connection is not established.');
    }
  };
  
  
  // const fetchLoggedInUsers = () => {
  //   //connect();
  //   const token = localStorage.getItem('token');
  //   fetch('http://localhost:8082/user/getLoggedInUsers', {
  //     method: 'GET',
  //     headers: {
  //       'Content-Type': 'application/json',
  //       'Authorization': `Bearer ${token}`,
  //     },
  //   })
  //     .then((response) => response.json())
  //     .then((data) => {
  //       setLoggedInUsers(data); 
  //     })
  //     .catch((error) => {
  //       console.error('Error fetching logged-in users:', error);
  //     });
  // };



  const handleFocus = () => {
    if (stompClient) {
      const focusData = {
        senderName: userData.receivername,
        receiverName: userData.username,
        status: 'FOCUS',
      };
      stompClient.send('/app/focus', {}, JSON.stringify(focusData));
    }
  };

  const [userData, setUserData] = useState({
    username: userEmail,
    receivername: '',
    connected: false,
    message: '',
  });


  const sendTypingEvent = () => {
    if (stompClient) {
      const typingData = {
        senderName: userData.username,
        receiverName: tab,
        status: 'TYPING',
      };
      stompClient.send('/app/typing', {}, JSON.stringify(typingData));
    }
  };

  const handleTyping = (event) => {
    setMessage(event.target.value);
    sendTypingEvent();
    if (tab !== 'CHATROOM' && seenMessages.has(tab)) {
      seenMessages.delete(tab);
      setSeenMessages(new Set([...seenMessages]));
    }
  };


  <input
    type="text"
    className="input-message"
    placeholder="Enter the message"
    value={userData.message}
    onChange={handleMessage}
    onFocus={() => {
      setReceiverFocus(true);
      handleFocus(); 
    }}
    onBlur={() => setReceiverFocus(false)}
  />


  

  const onTypingReceived = (payload) => {
    const typingData = JSON.parse(payload.body);
    if (typingData.status === 'TYPING') {
      if (tab === 'CHATROOM' && typingData.receiverName === 'CHATROOM') {
        setTypingStatus(typingData.senderName === userData.username ? '' : `${typingData.senderName} is typing...`);
      } else if (typingData.receiverName === userData.username) {
        setTypingStatus(`${typingData.senderName} is typing...`);
      } else {
        setTypingStatus('');
      }
    }
  };

  const handleMessageView = (chatId) => {
    if (tab === 'CHATROOM' && chatId) {
      const seenMessage = {
        senderName: userData.username,
        receiverName: tab, 
        status: 'SEEN',
        messageId: chatId, 
      };
      stompClient.send('/app/seen', {}, JSON.stringify(seenMessage));
      const updatedSeenMessages = new Set(seenMessages);
      updatedSeenMessages.add(chatId);
      setSeenMessages(updatedSeenMessages);
    }
  };




  return (
    <div className="container">
      {stompClient && userData.connected ? (
        <div className="chat-box">
          <div className="member-list">
            <ul>
              <li
                onClick={() => {
                  setTab('CHATROOM');
                }}
                className={`member ${tab === 'CHATROOM' && 'active'}`}
              >
                Chatroom
              </li>
              {[...privateChats.keys()].map((name, index) => (
                <li
                  onClick={() => {
                    setTab(name);
                  }}
                  className={`member ${tab === name && 'active'}`}
                  key={index}
                >
                  {name}
                </li>
              ))}
            </ul>
          </div>
          {tab === 'CHATROOM' && (
            <div className="chat-content">
              <ul className="chat-messages">
                {publicChats.map((chat, index) => (
                  <li
                    className={`message ${chat.senderName === userData.username && 'self'
                      }`}
                    key={index}
                    onClick={() => handleMessageView(chat.id)}
                  >
                    {chat.senderName !== userData.username && (
                      <div className="avatar">{chat.senderName}</div>
                    )}
                    <div className="message-data">{chat.message}</div>
                    {chat.senderName === userData.username && (
                      <div className="avatar self">{chat.senderName}</div>
                    )}
                    {index === publicChats.length - 1 && seenMessages.has(chat.id) && (
                      <span className="seen-notification">Seen</span>
                    )}
                  </li>
                ))}
              </ul>
              <div className="typing-indicator">
                <p>{typingStatus}</p>
              </div>
              <div className="send-message">
                <input
                  type="text"
                  className="input-message"
                  placeholder="enter the message"
                  value={userData.message}
                  onChange={handleMessage}
                  onKeyDown={handleTyping}
                />
                <button
                  type="button"
                  className="send-button"
                  onClick={sendValue}
                >
                  send
                </button>
              </div>
            </div>
          )}
          {tab !== 'CHATROOM' && (
            <div className="chat-content">
              <ul className="chat-messages">
                {[...privateChats.get(tab)].map((chat, index) => (
                  <li
                    className={`message ${chat.senderName === userData.username && 'self'
                      }`}
                    key={index}
                  >
                    {chat.senderName !== userData.username && (
                      <div className="avatar">{chat.senderName}</div>
                    )}
                    <div className="message-data">{chat.message}</div>
                    {chat.senderName === userData.username && (
                      <div className="avatar self">{chat.senderName}</div>
                    )}
                  </li>
                ))}
              </ul>
              <div className="typing-indicator">
                <p>{typingStatus}</p>
              </div>
              <div className="send-message">
                <input
                  type="text"
                  className="input-message"
                  placeholder="enter the message"
                  value={userData.message}
                  onChange={handleMessage}
                  onKeyDown={handleTyping}
                />
                <button
                  type="button"
                  className="send-button"
                  onClick={sendPrivateValue}
                >
                  send
                </button>
              </div>
            </div>
          )}
        </div>
      ) : null}
    </div>
  );
};

export default ChatRoom;
