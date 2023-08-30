import React, { useEffect, useRef, useState } from "react";
import { useParams } from "react-router-dom";
import { fetchFn } from "../../NetworkUtils";
import { API_URL } from "../../Constants";
import moment from "moment/moment";
import { fetch_General } from "../../networkFns/fetchFns";
import MySellComp2 from "../sell-service/MySellComp copy";
import KakaoMapBoardUpdate from "../kakaoMapComponents/KakaoMapBoardUpdate";

function MemberDetail() {
  const [member, setMember] = useState(null);
  const [editing, setEditing] = useState(false); 
  const [sellview, setSellview] = useState(true); 
  const [newName, setNewName] = useState("");
  const [mySellList, setMySellList] = useState([]);
  const [myChatList, setMyChatList] = useState([]);
  const [latitude, setLatitude] = useState();
  const [longitude, setLongitude] = useState();
  const setPositionData = { setLatitude, setLongitude };

  const username = useParams().username;
  const password = useRef();
  const password2 = useRef();
  const orgPassword = useRef();

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

  const handleCanleClick = () => {
    setEditing(false)}

  const handleSaveClick = () => {
      const dto = {
      username : username,
      password : password.current.value,
      password2 : password2.current.value,
      orgPassword : orgPassword.current.value,
      longitude : longitude,
      latitude : latitude,
      name : newName
      }

      if(password && password2 && orgPassword && longitude && latitude && newName){
        fetch_General("PUT", `${API_URL}/user-service/users`, dto)
        .then((data)=>{
          if (data === undefined) {
            return;
          }
          console.log(data)
        localStorage.setItem("latitude", `${data.latitude}`)
        localStorage.setItem("longitude", `${data.longitude}`)
        alert("수정완료")
        window.location.reload();
        })
      } else{
        alert("모든 필수 항목을 작성해주세요")
      }
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
      {/* 기본 정보 */}
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '20px' }}>
        <div>
          <img
            style={{ height: 'auto', width: '300px', display: 'inline-block' }}
            src={userPicture ? userPicture : '/img/nego1.png'} alt="preview"
          />
        </div>
        {editing? (
         <>
         <div style={{textAlign: 'right'}}>
         <input style={{ fontSize: 50 }} type="text" placeholder="(*필수)닉네임" value={newName} onChange={(e) => setNewName(e.target.value)} /><br/>
         <input style={{ fontSize: 50 }} type="text" placeholder="(*필수)기존비밀번호" ref={orgPassword}/><br/>
         <input style={{ fontSize: 50 }} type="text" placeholder="(*필수)비밀번호" ref={password}/><br/>
         <input style={{ fontSize: 50 }} type="text" placeholder="(*필수)비밀번호확인" ref={password2}/>
         </div>
         </>
        ):<div style={{ textAlign: 'right' }}>
        <p style={{ fontSize: 50 }}>아이디 : {member.username}</p>
        <p style={{ fontSize: 50 }}>닉네임 : {editing ? <input style={{ fontSize: 50 }} type="text" value={newName} onChange={(e) => setNewName(e.target.value)} /> : member.name}</p>
        <p style={{ fontSize: 50 }}>가입일 : {moment(member.createAt).format("YYYY-MM-DD")}</p>
        <p style={{ fontSize: 50}}>회원 수정일 : {moment(member.updateAt).format("YYYY-MM-DD")}</p>
      </div>}
        
      </div>

      {editing ? (
        <>
          <div><KakaoMapBoardUpdate setPositionData={setPositionData} /></div>
          <button className='btn-lg' style={{ fontSize: 20, marginLeft: "830px" }} onClick={handleSaveClick}>프로필 저장</button>
          <button className='btn-lg' style={{ fontSize: 20, marginLeft: "20px" }} onClick={handleCanleClick}>프로필 취소</button>
        </>
      ) : (
        <>
        <button className='btn-lg' style={{ fontSize: 20, marginLeft: "45em" }} onClick={handleEditClick}>프로필 수정</button>

         {/* 판매목록 및 채팅목록 보기 버튼 */}
      <div style={{ display: 'flex', justifyContent: 'center', marginTop: '20px' }}>
      <button className='btn-lg' style={{ fontSize: 20, alignItems: "center" }} onClick={SellListView}>판매목록 보기</button>
      <button className='btn-lg' style={{ fontSize: 20, alignItems: "center", marginLeft: '20px' }} onClick={MyChatView}>채팅목록 보기</button>
      </div>

      {/* 판매목록 또는 채팅목록 */}
      {sellview ? (
        <div className="routes" >
          {mySellList !== undefined && mySellList !== null && mySellList.length > 0 &&
            mySellList.map((mySell, index) => <MySellComp2 key={index} mySell={mySell} />)}
        </div>
      ) :
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
     }
    </>
      )}
      <br />
    </>
  )}
</div>

  );
}

export default MemberDetail;
