import React from 'react'

function MemberLogout() {


 
        localStorage.setItem("jwt", null);
        window.location.href = "/login"
  


  return (
    <div>


    </div>
  )
}

export default MemberLogout