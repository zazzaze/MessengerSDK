import SwiftUI

public struct MessageTextInput: View {
    @State private var inputText: String = ""
    let onSendMessageTap: (_ message: String) -> Void
    private let textInputBackground = Color(red: 233.0 / 255, green: 243.0 / 255, blue: 255.0 / 255)
    public var body: some View {
        VStack {
            HStack(alignment: .center) {
                TextField(
                    "Сообщение",
                    text: .init(
                        get: { inputText },
                        set: { newValue in
                            withAnimation(.easeInOut(duration: 0.1)) { inputText = newValue }
                        }
                    )
                )
                    .padding(.vertical, 10)
                    .padding(.horizontal, 10)
                    .background(textInputBackground)
                    .cornerRadius(20)
                    .foregroundColor(.blue)
                    if inputText.isEmpty == false {
                        Button(
                            action: {
                                onSendMessageTap(inputText)
                                withAnimation { inputText = "" }
                            }
                        ) {
                            Image(systemName: "paperplane.circle.fill")
                                .resizable()
                                .frame(width: 35, height: 35)
                                .foregroundColor(.blue)
                                .symbolRenderingMode(.hierarchical)
                                .transition(.move(edge: .trailing))
                        }
                    }

            }
            .padding()
        }
        .background(.clear)
    }
}

#if DEBUG
struct MessageTextInput_Previews: PreviewProvider {
    static let backgroundColor = Color(red: 218.0/255, green: 218.0/255, blue: 218.0/255)
    static let mainBackgroundColor = Color(red: 253.0/255, green: 255.0/255, blue: 217.0/255)
    static var previews: some View {
        MessageTextInput { _ in }
            .background(backgroundColor)
            .previewLayout(.sizeThatFits)

        VStack {
            Spacer()
            MessageTextInput { _ in }
                .padding(.bottom, 40)
                .background(backgroundColor)
                .cornerRadius(25, corners: .topRight)
                .cornerRadius(25, corners: .topLeft)
        }
        .ignoresSafeArea()

        VStack {
            Spacer()
            VStack {
                ChatBubble(direction: .left(drawTail: false)) {
                    Text("Hello!")
                        .padding(.vertical, 10)
                        .padding(.horizontal, 15)
                        .background(.blue)
                        .foregroundColor(.white)
                }
                ChatBubble(direction: .left(drawTail: true)) {
                    Text("How are you?")
                        .padding(.vertical, 10)
                        .padding(.horizontal, 15)
                        .background(.blue)
                        .foregroundColor(.white)
                }
                ChatBubble(direction: .right(drawTail: false)) {
                    Text("Hello!")
                        .padding(.vertical, 10)
                        .padding(.horizontal, 15)
                        .background(.green)
                        .foregroundColor(.white)
                }
                ChatBubble(direction: .right(drawTail: true)) {
                    Text("I'm ok. And You?")
                        .padding(.vertical, 10)
                        .padding(.horizontal, 15)
                        .background(.green)
                        .foregroundColor(.white)
                        .transition(.slide)
                }
            }
            MessageTextInput { _ in }
                .padding(.bottom, 20)
                .background(backgroundColor)
                .border(width: 0.5, edges: [.top], color: .black)
        }
        .background(mainBackgroundColor)
        .ignoresSafeArea()
    }
}
#endif
