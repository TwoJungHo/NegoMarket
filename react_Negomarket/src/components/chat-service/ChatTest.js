import React from 'react'
import { Link } from 'react-router-dom'

function ChatTest() {
  return (
    <div>
        <Link to={"/chatwith/jangwonyoung"}>jangwonyoung</Link>
        <br/>
        <Link to={"/chatwith/jenny"}>jenny</Link>
        
    </div>
  )
}

export default ChatTest