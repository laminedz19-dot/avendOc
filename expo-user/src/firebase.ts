import { getApp, getApps, initializeApp } from 'firebase/app';
import { getAuth } from 'firebase/auth';
import { getFirestore } from 'firebase/firestore';

const firebaseConfig = {
  apiKey: 'AIzaSyCejMerwEZ7AeosqxYZMihUjF_ZM-UeX-Q',
  authDomain: 'achridz-2c628.firebaseapp.com',
  projectId: 'achridz-2c628',
  storageBucket: 'achridz-2c628.firebasestorage.app',
  messagingSenderId: '176930344690',
  appId: '1:176930344690:android:246865186aca2df59b0a6b',
};

export const app = getApps().length ? getApp() : initializeApp(firebaseConfig);

export const auth = getAuth(app);

export const db = getFirestore(app);
