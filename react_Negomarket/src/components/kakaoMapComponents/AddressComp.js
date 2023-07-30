import React from "react";
const { kakao } = window;

function AddressComp(props) {
  
   

  function positionChg() {
    const x = props.add.x;
    const y = props.add.y;
    props.map.setCenter(new kakao.maps.LatLng(y, x));
    props.marker.setPosition(props.map.getCenter());
  }

  return (
    <div>
      <button onClick={positionChg} className="mapselect border" style={{marginLeft: '7.8%'}}>
        - {props.add.address_name}
      </button>
    </div>
  );
}

export default AddressComp;
