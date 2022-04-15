import SwiftUI
import MessengerSDK

struct RegisterLoginView: View {
    @State var login: String = ""
    @State var password: String = ""

    @State var registerLogin: String = ""
    @State var registerPassword: String = ""
    @State var userName: String = ""
    @State var showMessenger: Bool = false

    @State var messenger: MessengerSDK?
    var body: some View {
        NavigationView {
            VStack {
                VStack {
                TextField("Login", text: $login)
                TextField("Password", text: $password)
                Button("Log in") {
                    authUser()
                }
            }
                NavigationLink("", isActive: $showMessenger, destination: {
                    if let sdk = messenger {
                        ContentView(sdk: sdk)
                    }
                    else { EmptyView() }
                }
            )
                VStack {
                    Line()
                        .stroke(style: StrokeStyle(lineWidth: 1, dash: [5]))
                        .frame(height: 1)
                    Group {
                        TextField("Register login", text: $registerLogin)
                        TextField("Register password", text: $registerPassword)
                        TextField("Name", text: $userName)
                        Button("Register") {
                            regUser()
                        }
                    }
                }
                .navigationBarHidden(true)
                .padding(.horizontal, 50)
                .padding(.vertical, 40)
            }
        }
    }

    private func authUser() {
        let url = URL(string: "http://localhost:8070/auth")!
        var urlRequest = URLRequest(url: url)
        urlRequest.httpMethod = "POST"
        urlRequest.httpBody = try! JSONSerialization.data(
            withJSONObject: ["login": login, "password": password],
            options: .fragmentsAllowed
        )

        let task = URLSession.shared.dataTask(with: urlRequest) { data, response, error in
            guard
                let data = data,
                let json = try? JSONSerialization.jsonObject(with: data, options: .fragmentsAllowed) as? [String: String],
                let token = json["Token"],
                let uid = json["Uid"]
            else {
                return
            }

            DispatchQueue.main.async {
                messenger = .init(configuration: .init(apiKey: "", userId: uid, token: token))
                DispatchQueue.main.asyncAfter(deadline: .now() + 1) {
                    showMessenger = true
                }
            }
        }
        task.resume()
    }

    private func regUser() {
        let url = URL(string: "http://localhost:8070/reg")!
        var urlRequest = URLRequest(url: url)
        urlRequest.httpMethod = "POST"
        urlRequest.httpBody = try! JSONSerialization.data(
            withJSONObject: ["name": userName, "login": registerLogin, "password": registerPassword], options: .fragmentsAllowed)

        let task = URLSession.shared.dataTask(with: urlRequest) { data, response, error in
            print(error)
            print(data)
            print(response)
        }

        task.resume()
    }
}

private struct Line: Shape {
    func path(in rect: CGRect) -> Path {
        var path = Path()
        path.move(to: CGPoint(x: 0, y: 0))
        path.addLine(to: CGPoint(x: rect.width, y: 0))
        return path
    }
}

struct RegisterLoginView_Previews: PreviewProvider {
    static var previews: some View {
        RegisterLoginView()
    }
}
