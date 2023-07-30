import React from 'react'
import { API_URL } from '../../Constants';

function ChatTalk(props) {

  const isImageUrl = (url) => {
    const regex = new RegExp(`^${API_URL.replace(/\./g, '\\.').replace(/\//g, '\\/')}\\/imgfile-service\\/getimgdata\\?id=\\d+$`);
    return regex.test(url);
  };

 const formatTimestamp = (timestamp) => {
    const date = new Date(timestamp);
    return date.toLocaleString();
  };

  return (
    <div>
    {isImageUrl(props.object.message) ? 
      <div style={{width: "250px"}}>
        
        <span style={{fontSize: '5px'}}>{formatTimestamp(props.object.sendAt)}</span><br/><strong>{props.object.sender + " : "}</strong><br/>
        <img src={props.object.message} alt="Uploaded" style={{width: '200px', height: 'auto'}}/>
      </div>
    :  
      <div style={{width: "250px"}}>
      <div style={{fontSize: '5px'}}>
        {formatTimestamp(props.object.sendAt)}</div>
        <strong>{props.object.sender + " : "}</strong>
        {props.object.message}
      </div>
    }
    </div>
  )
}

export default ChatTalk