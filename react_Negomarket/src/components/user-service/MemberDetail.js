import React, { useEffect, useState } from "react";
import { Link, useParams } from "react-router-dom";
import { fetchFn } from "../../NetworkUtils";
import { API_URL } from "../../Constants";
import moment from "moment/moment";

function MemberDetail() {
  const [member, setMember] = useState(null);
  const [editing, setEditing] = useState(false); 
  const [newName, setNewName] = useState("");

  const username = useParams().username;

  const userPicture = `${API_URL}/user-service/pic/${username}`
  
  useEffect(() => {
    fetchFn(
      "GET",
      `http://localhost:8000/user-service/users/${username}`,
      null
    ).then((data) => {
      console.log(data);
      setMember(data);
      setNewName(data.name);
    });
  }, [username]);

  const handleEditClick = () => {
    setEditing(true);
  };

  const handleSaveClick = () => {
    fetchFn(
      "PUT",
      `http://localhost:8000/user-service/users/${username}`,
      { name: newName } 
    ).then(() => {
      setEditing(false); 
    });
  };

  return (
    <div>
      <img
        style={{ height: 'auto', width: '200px', display: 'inline-block', marginLeft: '10%'}}
        src={userPicture ? userPicture : '/img/nego1.png'} alt="preview"/>
      {member !== null && (
        <>
          <div>
            {editing ? (
              <input type="text" value={newName} onChange={(e) => setNewName(e.target.value)}/>)
              :( <p><span className="squaretxt1">이름 : </span>{member.username}</p>)}
              <p><span className="squaretxt1" style={{fontSize: 50}}>아이디 : </span>{member.name}</p>
              <p><span className="squaretxt1">가입일</span>{moment(member.createAt).format("YYYY-MM-DD")}</p>
              <p><span className="squaretxt1">회원 수정일</span>{moment(member.updateAt).format("YYYY-MM-DD")}</p>
          </div>
          {editing ? (
              <button onClick={handleSaveClick}>프로필 저장</button>) : (
              <button onClick={handleEditClick}>프로필 수정</button>
          )}
        </>
      )}
    </div>
  );
}

export default MemberDetail;
