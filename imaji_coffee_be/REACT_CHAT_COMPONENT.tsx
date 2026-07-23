// ============================================
// React Chat Component - Quick Setup Guide
// ============================================
// File: src/components/Chat.tsx

import React, {useEffect, useRef, useState} from 'react';
import SockJS from 'sockjs-client';
import {Client as StompClient} from '@stomp/stompjs';

interface ChatMessage {
    id: number;
    conversationId: number;
    content: string;
    senderName: string;
    senderType: 'USER' | 'ADMIN';
    senderId: number;
    createdAt: string;
}

interface ChatProps {
    customerId: number;
    conversationId: number;
    userName: string;
}

export default function Chat({ customerId, conversationId, userName }: ChatProps) {
    const [messages, setMessages] = useState<ChatMessage[]>([]);
    const [inputValue, setInputValue] = useState('');
    const [connected, setConnected] = useState(false);
    const [error, setError] = useState<string | null>(null);
    const stompClientRef = useRef<any>(null);
    const messagesEndRef = useRef<HTMLDivElement>(null);

    // Auto-scroll to latest message
    const scrollToBottom = () => {
        messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' });
    };

    useEffect(() => {
        scrollToBottom();
    }, [messages]);

    // Initialize WebSocket connection
    useEffect(() => {
        console.log('Connecting to WebSocket...');
        const socket = new SockJS('http://localhost:8080/api/v1');
        const client = StompClient.over(socket);

        client.configure({
            reconnectDelay: 5000,
            heartbeatIncoming: 4000,
            heartbeatOutgoing: 4000,
        });

        client.onConnect = () => {
            console.log('Connected to STOMP broker');
            setConnected(true);
            setError(null);

            // Subscribe to private message queue for this conversation
            client.subscribe(
                `/user/queue/chat/messages/${conversationId}`,
                (message) => {
                    const chatMessage: ChatMessage = JSON.parse(message.body);
                    console.log('Message received:', chatMessage);
                    setMessages((prev) => [...prev, chatMessage]);
                }
            );

            // Subscribe to error queue
            client.subscribe('/user/queue/errors', (message) => {
                console.error('Error from server:', message.body);
                setError(message.body);
            });
        };

        client.onStompError = (frame) => {
            console.error('STOMP Error:', frame.headers['message'], frame.body);
            setError('Connection error. Please refresh the page.');
            setConnected(false);
        };

        client.onDisconnect = () => {
            console.log('Disconnected from STOMP broker');
            setConnected(false);
        };

        client.activate();
        stompClientRef.current = client;

        return () => {
            if (stompClientRef.current?.connected) {
                stompClientRef.current.deactivate();
            }
        };
    }, [conversationId]);

    const sendMessage = () => {
        if (!inputValue.trim()) return;
        if (!stompClientRef.current?.connected) {
            setError('Not connected. Please wait...');
            return;
        }

        try {
            stompClientRef.current.publish({
                destination: `/api/v1/app/chat.sendMessage/${conversationId}`,
                headers: { 'content-type': 'application/json' },
                body: JSON.stringify({
                    content: inputValue,
                    senderName: userName,
                    senderType: 'USER',
                    senderId: customerId,
                }),
            });
            setInputValue('');
            setError(null);
        } catch (err) {
            console.error('Failed to send message:', err);
            setError('Failed to send message. Try again.');
        }
    };

    const handleKeyPress = (e: React.KeyboardEvent<HTMLInputElement>) => {
        if (e.key === 'Enter' && !e.shiftKey) {
            e.preventDefault();
            sendMessage();
        }
    };

    return (
        <div className="chat-container">
            <div className="chat-header">
                <h2>Support Chat</h2>
                <div className={`status ${connected ? 'connected' : 'disconnected'}`}>
                    {connected ? '🟢 Connected' : '🔴 Disconnected'}
                </div>
            </div>

            {error && (
                <div className="error-banner">
                    <span>{error}</span>
                    <button onClick={() => setError(null)}>✕</button>
                </div>
            )}

            <div className="messages-container">
                {messages.length === 0 ? (
                    <div className="empty-state">
                        <p>No messages yet. Start the conversation!</p>
                    </div>
                ) : (
                    messages.map((msg) => (
                        <div
                            key={msg.id}
                            className={`message ${
                                msg.senderId === customerId ? 'sent' : 'received'
                            }`}
                        >
                            <div className="message-header">
                                <span className="sender-name">{msg.senderName}</span>
                                <span className="sender-type">
                                    {msg.senderType === 'ADMIN' ? '(Admin)' : ''}
                                </span>
                                <span className="timestamp">
                                    {new Date(msg.createdAt).toLocaleTimeString()}
                                </span>
                            </div>
                            <div className="message-content">{msg.content}</div>
                        </div>
                    ))
                )}
                <div ref={messagesEndRef} />
            </div>

            <div className="input-container">
                <input
                    type="text"
                    value={inputValue}
                    onChange={(e) => setInputValue(e.target.value)}
                    onKeyPress={handleKeyPress}
                    placeholder={connected ? 'Type your message...' : 'Connecting...'}
                    disabled={!connected}
                    className="message-input"
                />
                <button
                    onClick={sendMessage}
                    disabled={!connected || !inputValue.trim()}
                    className="send-button"
                >
                    Send
                </button>
            </div>
        </div>
    );
}

// ============================================
// CSS Styles
// ============================================
/*

.chat-container {
    display: flex;
    flex-direction: column;
    height: 100vh;
    max-width: 800px;
    margin: 0 auto;
    background: #f5f5f5;
    border-radius: 8px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
    overflow: hidden;
}

.chat-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 16px 20px;
    background: #fff;
    border-bottom: 1px solid #ddd;
}

.chat-header h2 {
    margin: 0;
    font-size: 18px;
    color: #333;
}

.status {
    font-size: 12px;
    padding: 4px 8px;
    border-radius: 4px;
    font-weight: 600;
}

.status.connected {
    background: #e8f5e9;
    color: #2e7d32;
}

.status.disconnected {
    background: #ffebee;
    color: #c62828;
}

.error-banner {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 12px 16px;
    background: #ffcdd2;
    color: #b71c1c;
    font-size: 14px;
    border-bottom: 1px solid #ef5350;
}

.error-banner button {
    background: none;
    border: none;
    color: #b71c1c;
    cursor: pointer;
    font-size: 18px;
    padding: 0;
}

.messages-container {
    flex: 1;
    overflow-y: auto;
    padding: 20px;
    display: flex;
    flex-direction: column;
    gap: 12px;
}

.empty-state {
    display: flex;
    align-items: center;
    justify-content: center;
    height: 100%;
    color: #999;
    font-size: 14px;
}

.message {
    display: flex;
    flex-direction: column;
    margin-bottom: 8px;
}

.message.sent {
    align-items: flex-end;
}

.message.received {
    align-items: flex-start;
}

.message-header {
    display: flex;
    gap: 8px;
    font-size: 12px;
    margin-bottom: 4px;
    color: #666;
}

.sender-name {
    font-weight: 600;
}

.sender-type {
    font-style: italic;
    color: #aaa;
}

.timestamp {
    font-size: 11px;
    color: #bbb;
}

.message-content {
    padding: 12px 16px;
    border-radius: 8px;
    max-width: 70%;
    word-wrap: break-word;
    line-height: 1.4;
}

.message.sent .message-content {
    background: #2196f3;
    color: white;
}

.message.received .message-content {
    background: white;
    color: #333;
    border: 1px solid #ddd;
}

.input-container {
    display: flex;
    gap: 8px;
    padding: 16px;
    background: white;
    border-top: 1px solid #ddd;
}

.message-input {
    flex: 1;
    padding: 12px 16px;
    border: 1px solid #ddd;
    border-radius: 4px;
    font-size: 14px;
    font-family: inherit;
    resize: none;
}

.message-input:focus {
    outline: none;
    border-color: #2196f3;
    box-shadow: 0 0 0 3px rgba(33, 150, 243, 0.1);
}

.message-input:disabled {
    background: #f5f5f5;
    color: #ccc;
}

.send-button {
    padding: 12px 20px;
    background: #2196f3;
    color: white;
    border: none;
    border-radius: 4px;
    font-weight: 600;
    cursor: pointer;
    font-size: 14px;
    transition: background 0.2s;
}

.send-button:hover:not(:disabled) {
    background: #1976d2;
}

.send-button:disabled {
    background: #ccc;
    cursor: not-allowed;
}

.send-button:active:not(:disabled) {
    transform: scale(0.98);
}

*/

// ============================================
// Usage Example
// ============================================
/*

import Chat from '@/components/Chat';

export default function ChatPage() {
    const customerId = 1; // Get from auth/context
    const conversationId = 1; // Get from params or state
    const userName = 'John Doe'; // Get from auth/context

    return (
        <div className="page">
            <Chat
                customerId={customerId}
                conversationId={conversationId}
                userName={userName}
            />
        </div>
    );
}

*/
