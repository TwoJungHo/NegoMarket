import React, { useEffect, useRef } from "react";
import { useParams } from "react-router-dom";
import { fetchFn } from "../../NetworkUtils";

function MemberDelete() {
  const username = useParams().username;
  const passwordRef = useRef(); //{current : input태그} 이런식으로 들어가 있음

  function onClickHandler(e) {
    e.preventDefault();
    const password = passwordRef.current.value;
    const dto = {
      username,
      password,
    };

    fetchFn("DELETE", "http://localhost:9005/api/member", dto)
    .then(data => {
      
      if(data===undefined){
        console.log(data);
        passwordRef.current.value="";
        return;
      }
      localStorage.setItem("jwt", null);
        window.location.href = "/"
    })
  }

  useEffect(()=>{
    passwordRef.current.focus();
  },[]);

  return (
    <div>
      
        <input value={username} readOnly />
        <br />
        <input ref={passwordRef} placeholder='비번을 입력하세요'type="password" /><br/>
        <button onClick={onClickHandler}>삭 to the 제</button>
     
    </div>
  );
}

export default MemberDelete;
