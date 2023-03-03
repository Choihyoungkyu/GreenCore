import Head from "next/head";
import styles from "@/styles/Home.module.scss";
import AppLayout from "@/layout/AppLayout";

export default function Home() {
  return (
    <AppLayout>
      <Head>
        <title>Create Next App</title>
        <meta name='description' content='Generated by create next app' />
        <meta name='viewport' content='width=device-width, initial-scale=1' />
        <link rel='icon' href='/favicon.ico' />
      </Head>

      <main className={styles.main}>
        안뇽
        <button className='bg-blue-500 px-3 rounded'>hi</button>
      </main>
    </AppLayout>
  );
}
