import React, { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import { fetchFn } from "../../NetworkUtils";
import { API_URL } from "../../Constants";
import moment from "moment/moment";
import { fetch_General } from "../../networkFns/fetchFns";
import MySellComp2 from "../sell-service/MySellComp copy";

function MemberDetail() {
  const [member, setMember] = useState(null);
  const [editing, setEditing] = useState(false); 
  const [sellview, setSellview] = useState(true); 
  const [newName, setNewName] = useState("");
  const [mySellList, setMySellList] = useState([]);
  const [myChatList, setMyChatList] = useState([]);

  const username = useParams().username;

  const userPicture = `${API_URL}/user-service/pic/${username}`
  
  useEffect(() => {
    
    /* 나의 프로필 정보 가져오기 */
    fetchFn(
      "GET",
      `http://localhost:8000/user-service/users/${username}`,
      null
    ).then((data) => {
      setMember(data);
      setNewName(data.name);
    });
  }, [username]);

  const handleEditClick = () => {
    setEditing(true);
  };

  const handleSaveClick = () => {
    
      setEditing(false); 
    
  };

  const SellListView = () => {
    
    setSellview(true); 
    /* 나의 판매목록 가져오기 */
    (async () => {
      const sellList = await fetch_General(
        "GET",
        `${API_URL}/sell-service/sells/mylist`
      );
      setMySellList(sellList)})();
};

  const MyChatView = () => {
    /* 나의 채팅 목록 가져오기 */
    (async () => {
      const chatList = await fetch_General(
        "GET",
        `${API_URL}/chat-service/findrooms`
      );
      console.log(chatList)
      setMyChatList(chatList)})();
    setSellview(false); 
};

function openPopup(path) {
  const newWindow = window.open(path, '_blank', 'width=560,height=700,noopener,noreferrer');
  if (newWindow) newWindow.opener = null;
}

  return (
    
    <div>
      {member !== null && (
        <>
        {editing ? (
              <button className = 'btn-lg' style={{fontSize: 20, marginLeft: "45em"}} onClick={handleSaveClick}>프로필 저장</button>) : (
                <button className = 'btn-lg' style={{fontSize: 20, marginLeft: "45em"}} onClick={handleEditClick}>프로필 수정</button>
          )}<br/>
          <img
        style={{ height: 'auto', width: '200px', display: 'inline-block'}}
        src={userPicture ? userPicture : '/img/nego1.png'} alt="preview"/>
          <div>
              <p style={{fontSize: 50}}>아이디 : {member.name}</p>
            {editing ? (
              // eslint-disable-next-line jsx-a11y/anchor-is-valid
              <><a style={{fontSize: 50}}>이름 : </a><input style={{ fontSize: 50 }} type="text" value={newName} onChange={(e) => setNewName(e.target.value)} /><br/>
              <input style={{ fontSize: 50 }} type="text" placeholder="비밀번호" /><br/>
              <input style={{ fontSize: 50 }} type="text" placeholder="비밀번호확인" />
              </>
              ):( <><p style={{ fontSize: 50 }}>이름 : {member.username}</p>
              <p style={{ fontSize: 50 }}>가입일 : {moment(member.createAt).format("YYYY-MM-DD")}</p>
              <p style={{ fontSize: 50 }}>회원 수정일 : {moment(member.updateAt).format("YYYY-MM-DD")}</p>

              <button className = 'btn-lg' style={{fontSize: 20}} onClick={SellListView}>판매목록 보기</button>
              <button className = 'btn-lg' style={{fontSize: 20}} onClick={MyChatView}>채팅목록 보기</button>

              {sellview? (
              <div className="routes">
              {mySellList !==undefined && mySellList !== null && mySellList.length > 0 &&
               mySellList.map((mySell, index) => <MySellComp2 key={index} mySell={mySell}/>)}
           </div>
              ): 
              <div>
              {myChatList.length !== 0 && myChatList.map(
                  (mychat, index) => 
                  <div key={index}>
                    <span>채팅방이름 : {mychat.title}</span><br/>
                      <span>{mychat.requestSubject === mychat.username1 ? mychat.username2 : mychat.username1}</span>
                      <button onClick={
                        () => {
                          let buyer;
                          if(mychat.requestSubject === mychat.username1) {
              
                            buyer = mychat.username2
              
                        } else {buyer = mychat.username1}
                      
                        openPopup(`/chatwith/${buyer}`)} 
                        }><img src='/img/chatting.png' style={{widthd: '30px', height: '30px'}} alt="채팅이미지"/></button>
                  </div>)}
                  </div>
              }</>
              )}
          </div>
        </>
      )}
    </div>
  );
}

export default MemberDetail;
