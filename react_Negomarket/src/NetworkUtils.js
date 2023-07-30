export function fetchFn(method, url, dto) {
  let options = {
    method: method,
    headers: {
      "Content-Type": "application/json",
    },
  };

  const token = localStorage.getItem("jwt");
  if (token !== null && token.length > 0) {
    options.headers.Authorization = "Bearer " + token;
  }

  if (dto) {
    options.body = JSON.stringify(dto);
  }

  return fetch(url, options)
    .then((res) => {
      if (res.status === 403) {
        window.location.href = "/login";
      }

      if (!res.ok) {
        throw new Error("작동 실패!");
      }
      
      return res.json();
    })
    .catch((error) => {
      alert(error.message);
    });
}
