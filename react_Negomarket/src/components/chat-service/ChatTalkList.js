import React, { useEffect, useRef } from "react";
import ChatTalk from "./ChatTalk";

function ChatTalkList(props) {
  const chatRef = useRef(null);

  useEffect(() => {
    if (chatRef.current) {
      chatRef.current.scrollTop = chatRef.current.scrollHeight;
    }
  }, [props.messageList]);

  return (<>
    <h1><img src="/img/negochat.png" style={{width: '130px', height: '56px',marginTop: '-15px' ,marginBottom: '-14px'}}/></h1>
    <div className='chatMessageBox' ref={chatRef}>
      
      <div>
        {props.messageList.length > 0 && props.messageList.map((messageObject, index) => (
        <div className="chatMessage">  <ChatTalk key={index} object={messageObject} /> </div>
        ))}
      </div>
    </div>
    </>
  );
}

export default ChatTalkList;
