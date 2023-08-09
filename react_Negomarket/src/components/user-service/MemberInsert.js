import React, { useRef, useState } from "react";
import KakaoMapSignUp from "../kakaoMapComponents/KakaoMapSignUp";
import { fetch_multiForm } from "../../networkFns/fetchFns";

function MemberInsert() {
  const picFile = useRef();
  const username = useRef();
  const password = useRef();
  const password2 = useRef();
  const name = useRef();

  const [previewImage, setPreviewImage] = useState(null);
  const [latitude, setLatitude] = useState();
  const [longitude, setLongitude] = useState();
  const setPositionData = { setLatitude, setLongitude };

  localStorage.setItem("jwt", null);

  const submitSignUpRequest = () => {

    const formData = new FormData();
    formData.append("picFile", picFile.current.files[0]);
    formData.append("username", username.current.value);
    formData.append("password", password.current.value);
    formData.append("password2", password2.current.value);
    formData.append("name", name.current.value);
    formData.append("longitude", longitude);
    formData.append("latitude", latitude);


    console.log(formData.get("picFile"));
    console.log(formData.get("username"));
    console.log(formData.get("password"));
    console.log(formData.get("password2"));
    console.log(formData.get("latitude"));
    console.log(formData.get("longitude"));
    console.log(formData.get("name"));

    fetch_multiForm("POST", "http://localhost:8000/user-service/users", formData)
    .then((data) => {
    window.location.href = "/"
    })
    
  }

  function handleInputChange() {

    const file = picFile.current.files[0];



    // 그림 파일 입력시 미리 보기 생성
    if (file) {
      const reader = new FileReader();

      // file 읽기가 완료될 경우 실행되는 콜백함수
      reader.onloadend = () => {
        setPreviewImage(reader.result);
      };

      // 선택된 파일을 읽어서 파일의 내용이 직접 포함된 URL로 변환
      reader.readAsDataURL(file);
    } else {
      setPreviewImage(null);
    }

  }



  return (
    <div className="routes">
      <div style={{ width: '1080px'}}>
        <div className='imageBox-sm' style={{ margin: '3em 2em 2em 0em' }}><img style={{ height: '150px', width: '150px', }} src={previewImage ? previewImage : '/img/nego1.png'} alt='preview' /></div>
        <div className='components-inputs-sm' style={{ maxHeight: '300px', width: '650px', marginTop: '3em' }}>
          <input type='file' ref={picFile} onChange={handleInputChange} />
          <input placeholder="아이디" ref={username} />
          <input type="password" placeholder="비밀번호" ref={password} />
          <input type="password" placeholder="비밀번호 확인" ref={password2} />
          <input placeholder="이름" ref={name} />
        </div>
        <div id="signup-btn-container"><button id="signup-btn" onClick={submitSignUpRequest}>가입</button></div>
        </div>
        <div>
          <KakaoMapSignUp
            setPositionData={setPositionData} />
        </div>


      
    </div>
  );
}

export default MemberInsert;
