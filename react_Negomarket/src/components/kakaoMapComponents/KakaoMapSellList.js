import React, { useEffect, useRef, useState } from "react";
import { fetch_General } from "../../networkFns/fetchFns";
import { API_URL } from '../../Constants';
const { kakao } = window;

let marker;
let markers = [];

function KakaoMapSellList({ setRangedSellList }) {

  const [map, setMap] = useState(null);

  const [currentCircle, setCurrentCircle] = useState(null);

  const inputRange = useRef(0.0);

  const handleChange = (event) => {

    const circleproperty = {
      center: marker.getPosition(),  // 원의 중심좌표 입니다 
      radius: 0, // 미터 단위의 원의 반지름입니다 
      strokeWeight: 2, // 선의 두께입니다 
      strokeColor: '#000000', // 선의 색깔입니다
      strokeOpacity: 1, // 선의 불투명도 입니다 1에서 0 사이의 값이며 0에 가까울수록 투명합니다
      strokeStyle: 'dashed', // 선의 스타일 입니다
      fillColor: '#000000', // 채우기 색깔입니다
      fillOpacity: 0.3  // 채우기 불투명도 입니다   
    };

    let range = event.target.value;
    circleproperty.radius = range * 1000

    if (currentCircle) {
      currentCircle.setMap(null);
    }

    const circle = new kakao.maps.Circle(circleproperty);
    circle.setMap(map);

    setCurrentCircle(circle);

  }

  useEffect(() => {
    let localLongitude;
    let localLatitude
    if(localStorage.getItem("longitude") !== null && localStorage.getItem("longitude")){
      localLongitude = localStorage.getItem("longitude")
    } else {
      localLongitude = 126.9890501;
    }

    if(localStorage.getItem("latitude") !== null && localStorage.getItem("latitude")){
      localLatitude = localStorage.getItem("latitude")
    } else{
      localLatitude = 37.551159;
    }


    const initLongitude = localLongitude;
    const initLatitude = localLatitude;
    const container = document.getElementById("kmap");
    const options = {
      center: new kakao.maps.LatLng(initLatitude, initLongitude),
      level: 8
    };
    let kakaoMap = new kakao.maps.Map(container, options);
    marker = new kakao.maps.Marker({ position: kakaoMap.getCenter() });
    marker.setMap(kakaoMap);
    setMap(kakaoMap);
  }, []);




  const sellListSearchButtonHandler = async () => {
    if (currentCircle) {

      if (markers.length > 0) {
        for (let i = 0; i < markers.length; i++) {
          markers[i].setMap(null);
        }
        markers = [];
      }

      const sellList = await fetch_General("GET", `${API_URL}/sell-service/sells/findbyrange?range=${inputRange.current.value}`)
      if (sellList) {
        setRangedSellList(sellList);

        for (let i = 0; i < sellList.length; i++) {
          let markerImgSrc = '/img/sellmarker.png';
          let markerImgSize = new kakao.maps.Size(30, 30);
          let markerImgOption = { offset: new kakao.maps.Point(0, 0) }
          let markerImg = new kakao.maps.MarkerImage(
            markerImgSrc, markerImgSize, markerImgOption
          );
          let markerPosition = new kakao.maps.LatLng(sellList[i].latitude, sellList[i].longitude);
          let marker = new kakao.maps.Marker({
            position: markerPosition,
            image: markerImg
          });
          markers.push(marker);
        }
        for (let i = 0; i < markers.length; i++) {
          markers[i].setMap(map)
        }
      }
    }
  }

  return (
    <div>
      <div style={{ marginBottom: "0.3em" }}>
        <span>반경</span> <input style={{ width: "800px" }} ref={inputRange} type="range" min="0.5" max="20.0" step="0.1" defaultValue="10.0" onChange={handleChange} />
        <button className="btn" style={{ marginLeft: "7.2em" }} onClick={sellListSearchButtonHandler}>판매 목록 검색</button>
      </div>
      <div id="kmap" style={{ width: "1080px", height: "500px", marginBottom: "3em" }} />
    </div>
  );
}

export default KakaoMapSellList;