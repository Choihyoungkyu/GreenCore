import axios from 'axios';
import { useEffect } from 'react';
import { getCookieToken } from './cookies';
import { useAppDispatch, useAppSelector } from '@/core/hooks';
import { getAccessToken } from '@/core/user/userAPI';

import Toastify from 'toastify-js';
import message from '@/assets/message.json';
import toastifyCSS from '@/assets/toastify.json';
import { useRouter } from 'next/router';

const serverUrl =
	process.env.NODE_ENV == 'production'
		? process.env.APP_SERVER_URL
		: process.env.LOCAL_TYPE == 'development'
		? 'http://localhost:8080'
		: 'http://localhost:3000';

const instance = axios.create({
	baseURL: serverUrl + '/api',
	// timeout: 1000,

	headers: {
		'Content-Type': 'application/json',
		'Access-Control-Allow-Origin': '*',
		'Access-Control-Allow-Credentials': true,
		'Access-Control-Allow-Methods': 'GET,OPTIONS,PATCH,DELETE,POST,PUT',
		'Access-Control-Allow-Headers': '*',
		'Access-Control-Expose-Headers': '*'
		// 'X-CSRF-Token, X-Requested-With, X-Refresh-Token, Authorization, Accept, Accept-Version, Content-Length, Content-MD5, Content-Type, Date, X-Api-Version',
	},

	withCredentials: true
});

const AxiosInterceptor = ({ children }) => {
	const dispatch = useAppDispatch();
	const router = useRouter();
	const authType = useAppSelector((state) => state.common.authType);
	const accessToken = useAppSelector((state) => state.common.accessToken);

	useEffect(() => {
		const reqInterceptor = async (config) => {
			if (getCookieToken()) {
				config.headers['X-Refresh-Token'] = getCookieToken();
				config.headers['authorization'] = `Bearer ${accessToken}`;
			}

			return config;
		};

		const resInterceptor = (response) => {
			const status = response.data?.status;

			if (status === 401) {
				console.log('access token 만료!');
				if (getCookieToken()) dispatch(getAccessToken(authType));
			}

			if (status === 403) {
				console.log('유효한 토큰이 아님');
				router.push('/auth/login');
			}

			return response;
		};

		const errInterceptor = (error) => {
			const status = error.response?.status;

			if (error.code == 'ERR_NETWORK') {
				Toastify({
					text: message['500Error'],
					duration: message.MessageDuration,
					position: 'center',
					stopOnFocus: true,
					style: toastifyCSS.error
				}).showToast();
			}

			if (status === 403) {
				console.log('유효한 토큰이 아님!');
			}

			if (status == 404) {
				console.log('NOT FOUND');
			}

			if (status == 500) {
				Toastify({
					text: message['500Error'],
					duration: message.MessageDuration,
					position: 'center',
					stopOnFocus: true,
					style: toastifyCSS.error
				}).showToast();
			}

			return Promise.reject(error);
		};

		const requestInterceptor = instance.interceptors.request.use(reqInterceptor);
		const responseInterceptor = instance.interceptors.response.use(resInterceptor, errInterceptor);

		return () => {
			instance.interceptors.request.eject(requestInterceptor);
			instance.interceptors.response.eject(responseInterceptor);
		};
	}, [authType, accessToken]);

	return children;
};

export default instance;
export { AxiosInterceptor };
