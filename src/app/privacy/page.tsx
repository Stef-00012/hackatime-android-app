export default function Privacy() {
    return (
        <div className="p-3">
            <p className="text-4xl font-bold mb-1">Hackatime Android Privacy Policy</p>

            <p className="text-xl">This privacy policy explains how Hackatime Android collects, uses, and protects your information when you use our app.</p>

            <h2 className="text-2xl font-bold my-3">Access</h2>
            <p className="text-xl">In order to use the Hackatime Android app, the user must have a <a className="text-red!" href="https://hackatime.hackclub.com" target="_blank" rel="noopener">Hackatime</a> account and have the API key for the aforementioned account.</p>

            <h2 className="text-2xl font-bold my-3">Use</h2>
            <p className="text-xl">The app allows its users to view the data of their <a className="text-red!" href="https://hackatime.hackclub.com" target="_blank" rel="noopener">Hackatime</a> account. All interactions occur directly between the user&lsquo;s device and <a className="text-red!" href="https://hackatime.hackclub.com" target="_blank" rel="noopener">Hackatime</a>. The Hackatime Android app does not collect any data from user interactions.</p>

            <h2 className="text-2xl font-bold my-3">Login Data</h2>
            <p className="text-xl">Upon login, the Hackatime Android app stores the user&lsquo;s API key on the user&lsquo;s device in the app&lsquo;s private storage. This API key is used solely to keep the user logged in and avoid repeated authentication. This API key is only shared with <a className="text-red!" href="https://hackatime.hackclub.com" target="_blank" rel="noopener">Hackatime</a> to authenticate the HTTPs requests and with the app&lsquo;s server via HTTPs to allow background taks such as goals and motivational notifications.</p>

            <h2 className="text-2xl font-bold my-3">Ads</h2>
            <p className="text-xl">The Hackatime Android application does not contain any advertisement. The app only displays the data returned by the <a className="text-red!" href="https://hackatime.hackclub.com" target="_blank" rel="noopener">Hackatime</a> API.</p>

            <h2 className="text-2xl font-bold my-3">Contact</h2>
            <p className="text-xl">If you have any question about this privacy policy, please contact the following email address: <a className="text-red!" href="mailto:privacy@hackatime.stefdp.com">privacy@hackatime.stefdp.com</a>.</p>
        </div>
    )
}