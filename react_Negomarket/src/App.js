import { BrowserRouter, Route, Routes, useLocation } from 'react-router-dom';
import './App.css';
import ChatRoom from './components/chat-service/ChatRoom';
import Login from './components/auth/Login';
import MemberInsert from './components/user-service/MemberInsert';
import BoardInsert from './components/board-service/BoardInsert';
import Navbar from './components/Navbar';
import BoardUpdate from './components/board-service/BoardUpdate';
import BoardDetail from './components/board-service/BoardDetail';
import SellRangedList from './components/sell-service/SellRangedList';
import MySells from './components/sell-service/MySells';
import MyChatList from './components/chat-service/MyChatList';
import MemberDetail from './components/user-service/MemberDetail';

function App() {

function HeaderController() {
  const location = useLocation();
  const isChatRoomPage = location.pathname.startsWith("/chatwith/");
  
  return (
    <header>{isChatRoomPage ? '' : <Navbar/>}</header>
  )
}


  return (
    <div>
      
      <BrowserRouter>
      <HeaderController/>
      
      <div id='bodybox'>
      <div id='router_container'>
      <Routes>

        <Route path="/login" Component={Login}/>
        
        <Route path="/signup" Component={MemberInsert}/>

        <Route path="/mychatlist" Component={MyChatList}/>
        <Route path="/inrangeselllist" Component={SellRangedList}/>
        <Route path="/myselllist" Component={MySells}/>
        <Route path="/board/insert" Component={BoardInsert}/>
        <Route path="/board/update/:sellId" Component={BoardUpdate}/>
        <Route path="/board/detail/:sellId" Component={BoardDetail}/>
        
        <Route path="/chatwith/:username" Component={ChatRoom}/>
        <Route path="/users/:username" Component={MemberDetail}/>

        
      </Routes>
      </div>
      </div>

      </BrowserRouter>
    </div>
    
  );
}

export default App;
