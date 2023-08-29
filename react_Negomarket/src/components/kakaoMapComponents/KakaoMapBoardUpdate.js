/* eslint-disable react-hooks/exhaustive-deps */
import React, { useEffect, useRef, useState } from 'react'
import AddressComp from './AddressComp';

const { kakao } = window;

let geocoder =  new kakao.maps.services.Geocoder();
let marker;

function KakaoMapBoardUpdate({ setPositionData }) {

    const [map, setMap] = useState(null);
  const [addArr, setAddArr] = useState([]);

  const inputAddress = useRef();
  
function callback (result, status) {
  if (status === kakao.maps.services.Status.OK){
    console.log(result);
    setAddArr(result);
  }
}

function clickSearchEvent(){
  let address = inputAddress.current.value;
  geocoder.addressSearch(address, callback);
  
}

  useEffect(() => {
    let latlng;
    const container = document.getElementById("kmap");
    const options = { center: new kakao.maps.LatLng(localStorage.getItem("latitude"), localStorage.getItem("longitude"))};
    let kakaoMap = new kakao.maps.Map(container, options);
    marker = new kakao.maps.Marker({position: kakaoMap.getCenter()});
   
    
    marker.setMap(kakaoMap);
    kakao.maps.event.addListener(kakaoMap, 'click', 
    
    function(mouseEvent){

      latlng = mouseEvent.latLng;
      marker.setPosition(latlng);
      setPositionData.setLatitude(latlng.Ma);
      setPositionData.setLongitude(latlng.La);
    });
    
    setMap(kakaoMap);
  }, []);
  return (
    <div>
      
      <div id="kmap" style={{width: "1080px", height: "400px"}}></div>
      <img src="/img/pin2.png" style={{width: '40px', height:'auto'}} alt=""/>
      <input  className='border' style={{display: 'inline-block', width: '495px', height: '30px'}} ref={inputAddress} placeholder="거주지 선택                                           ex) 서울특별시 강남구 한남동"/><button className="button" onClick={clickSearchEvent}>검색</button>
      <div>{addArr.length > 0 && addArr.map((x, index) => <AddressComp key={index} map={map} marker={marker} add={x}/>)}</div>
    </div>
  )
}

export default KakaoMapBoardUpdate