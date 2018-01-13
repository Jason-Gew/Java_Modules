'use strict';

var usernamePage = document.querySelector('#username-page');
var chatPage = document.querySelector('#chat-page');
var usernameForm = document.querySelector('#usernameForm');
var messageForm = document.querySelector('#messageForm');
var messageInput = document.querySelector('#message');
var messageArea = document.querySelector('#messageArea');
var connectingElement = document.querySelector('.connecting');

var stompClient = null;
var username = null;

var colors = [
    '#2196F3', '#32c787', '#00BCD4', '#ff5652',
    '#ffc107', '#ff85af', '#FF9800', '#39bbb0'
];

function connect(event) {
    username = document.querySelector('#name').value.trim();

    if(username) {
        usernamePage.classList.add('hidden');
        chatPage.classList.remove('hidden');

        var socket = new SockJS('/ws');
        stompClient = Stomp.over(socket);

        stompClient.connect({}, onConnected, onError);
    }
    event.preventDefault();
}


function onConnected() {
    // Subscribe to the Public Topic
    stompClient.subscribe('/topic/public', onMessageReceived);

    // Tell your username to the server
    stompClient.send("/app/chat.addUser",
        {},
        JSON.stringify({sender: username, type: 'JOIN'})
    );

    connectingElement.classList.add('hidden');
}


function onError(error) {
    connectingElement.textContent = 'Could not connect to WebSocket server. Please refresh this page to try again!';
    connectingElement.style.color = 'red';
}


function sendMessage(event) {
    var messageContent = messageInput.value.trim();

    if(messageContent && stompClient) {
        var chatMessage = {
            sender: username,
            content: messageInput.value,
            type: 'CHAT'
        };

        stompClient.send("/app/chat.sendMessage", {}, JSON.stringify(chatMessage));
        messageInput.value = '';
    }
    event.preventDefault();
}


function onMessageReceived(payload) {
    var message = JSON.parse(payload.body);

    var messageElement = document.createElement('li');

    var datetime = getCurrentDatetime();

    if(message.type === 'JOIN') {
        messageElement.classList.add('event-message');
        message.content = ' User [ ' + message.sender + ' ] Joined @ ' + datetime;
    } else if (message.type === 'LEAVE') {
        messageElement.classList.add('event-message');
        message.content = ' User [ ' + message.sender + ' ] Left @ ' + datetime;
    } else {
        messageElement.classList.add('chat-message');

        var avatarElement = document.createElement('i');
        var avatarText = document.createTextNode(message.sender[0]);
        avatarElement.appendChild(avatarText);
        avatarElement.style['background-color'] = getAvatarColor(message.sender);

        messageElement.appendChild(avatarElement);

        var usernameElement = document.createElement('span');
        var usernameText = document.createTextNode(message.sender);
        usernameElement.appendChild(usernameText);
        messageElement.appendChild(usernameElement);
    }

    var textElement = document.createElement('p');
    var messageText = document.createTextNode(message.content);
    textElement.appendChild(messageText);

    messageElement.appendChild(textElement);

    messageArea.appendChild(messageElement);
    messageArea.scrollTop = messageArea.scrollHeight;
}


function getAvatarColor(messageSender) {
    var hash = 0;
    for (var i = 0; i < messageSender.length; i++) {
        hash = 31 * hash + messageSender.charCodeAt(i);
    }

    var index = Math.abs(hash % colors.length);
    return colors[index];
}

function getCurrentDatetime() {
    var month = null;
    var date = null;
    var hour = null;
    var minute = null;
    var second = null;
    var timestamp = new Date();
    if((timestamp.getMonth()+1) < 10)
        month = "0"+(timestamp.getMonth()+1);
    else
        month = (timestamp.getMonth()+1);

    if(timestamp.getDate() < 10)
        date = "0"+timestamp.getDate();
    else
        date = timestamp.getDate();

    if(timestamp.getHours() < 10)
        hour = "0"+timestamp.getHours();
    else
        hour = timestamp.getHours();

    if(timestamp.getMinutes() < 10)
        minute = "0"+timestamp.getMinutes();
    else
        minute = timestamp.getMinutes();

    if(timestamp.getSeconds() < 10)
        second = "0"+timestamp.getSeconds();
    else
        second = timestamp.getSeconds();

    var datetime = timestamp.getFullYear()+"-"+month+"-"+date+"  "+hour+":"+minute+":"+second;
    return datetime.toString();
}

usernameForm.addEventListener('submit', connect, true)
messageForm.addEventListener('submit', sendMessage, true)