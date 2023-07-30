import React from 'react'
import { BrowserRouter, Route, Routes } from 'react-router-dom'
import Navbar from './components/board-service/Navbar'
import Login from './components/auth/Login'
import MemberInsert from './components/user-service/MemberInsert'
import RateTest from './RateTest'
import BoardInsert from './components/board-service/BoardInsert'
import BoardUpdate from './components/board-service/BoardUpdate'
import BoardDetail from './components/board-service/BoardDetail'

function MainComponent() {
  return (
    <div>
        
      <header className='full-width'><Navbar/></header>
      
      <div>
      <Routes>
        <Route path="/login" Component={Login}/>
        <Route path="/signup" Component={MemberInsert}/>

        <Route path="/RateTest" Component={RateTest}/>
        
        <Route path="/board/insert" Component={BoardInsert}/>
        <Route path="/board/update/:sellId" Component={BoardUpdate}/>
        <Route path="/board/detail/:sellId" Component={BoardDetail}/>
        
        
        
      </Routes>
      </div>
      <img className='center1' src="/img/negounder.png"  style={{width: '300px', height: 'auto'}} />
      
    </div>
  )
}

export default MainComponent