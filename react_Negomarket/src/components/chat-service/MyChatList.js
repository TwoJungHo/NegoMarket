import React, { useEffect, useState } from 'react'
import { fetch_General } from '../../networkFns/fetchFns';
import { API_URL } from '../../Constants';
function MyChatList() {
const [myChatList, setMyChatList] = useState([]);


useEffect(() => {
    (async () => {
        const chatList = await fetch_General(
          "GET",
          `${API_URL}/chat-service/findrooms`
        );
        setMyChatList(chatList)})();

        




}, []

);

console.log(myChatList);

function openPopup(path) {
    const newWindow = window.open(path, '_blank', 'width=560,height=700,noopener,noreferrer');
    if (newWindow) newWindow.opener = null;
  }




  return (
    <div>
{myChatList.length !== 0 && myChatList.map(
    (mychat, index) => 
    <div key={index}>
        <span>{mychat.requestSubject === mychat.username1 ? mychat.username2 : mychat.username1}</span>
        <button onClick={
          () => {
            let buyer;
            if(mychat.requestSubject === mychat.username1) {

              buyer = mychat.username2

          } else {buyer = mychat.username1}
        
          openPopup(`/chatwith/${buyer}`)} 
          }><img src='/img/chatting.png' style={{widthd: '30px', height: '30px'}} alt='채팅이미지'/></button>
    </div>)}
    </div>
  )
}

export default MyChatList