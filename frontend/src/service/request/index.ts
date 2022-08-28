export interface InternalInResponseType {
  data: any;
  message: string;
  status: number;
}

export default function request(
  input: RequestInfo,
  init?: RequestInit | undefined
): Promise<InternalInResponseType> {
  const rinit = {
    ...init,
    headers: {
      ...init?.headers,
      "x-pimp-source": "fproject-room",
    },
  };
  return fetch(input, rinit).then((res) => {
    if (res.status === 200) {
      return res.json();
    } else {
      throw Error(res.statusText);
    }
  });
}

export const serialize = (data: any) => {
  // 传入对象，序列化+encode，输出拼在url后充当querystring；
  if (!data) {
    return "";
  }
  const pairs = [];
  for (let name of Object.keys(data)) {
    if (typeof data[name] === "function") {
      continue;
    }
    if (data[name] === undefined) {
      continue;
    }
    let value = data[name];

    if (Object.prototype.toString.call(value) === "[object Object]") {
      value = JSON.stringify(value);
    } else {
      if (Object.prototype.toString.call(value) === "[object Array]") {
        if (!value.length) {
          continue;
        } else {
          for (let i = 0; i < value.length; i++) {
            pairs.push(`${name}=${value[i]}`);
          }
          continue;
        }
      }
    }

    name = encodeURIComponent(name);
    value = encodeURIComponent(value);
    pairs.push(`${name}=${value}`);
  }
  return pairs.join("&");
};

export const addParams = (url: string, kv: any) => {
  try {
    if (!url || typeof url !== "string") {
      return;
    }
    if (!kv) {
      return url;
    }
    if (url.indexOf(kv) > -1) {
      return url;
    }
    if (url.indexOf("?") > -1) {
      url += `&${kv}`;
    } else {
      url += `?${kv}`;
    }
    // console.log('url-->', url);

    return url;
  } catch (e) {
    console.error(e);
    return url;
  }
};

export const addParamsObj = (url: string, kvObj: any) => addParams(url, serialize(kvObj));
