import SwiftUI
import shared

public protocol ChatListViewModel: ObservableObject {
    var messages: [Message] { get }
    var shouldScrollToFirst: Bool { get }
    var drawForeignMessageMessageTitle: Bool { get }
    func didScrollToLast()
    func didScrollToFirst()
}

public struct ChatList<ViewModel>: View where ViewModel: ChatListViewModel {
    @ObservedObject private var viewModel: ViewModel
    private let textBackgroundColor = Color(red: 237, green: 237, blue: 237, opacity: 0.6)
    public var body: some View {
        GeometryReader { geometry in
            VStack {
                if viewModel.messages.isEmpty {
                    HStack {
                        Spacer()
                        VStack(alignment: .center) {
                            Spacer()
                            Text("No messages yet...")
                                .padding(.horizontal, 15)
                                .padding(.vertical, 3)
                                .background(textBackgroundColor)
                                .cornerRadius(10, corners: .allCorners)
                            Spacer()
                        }
                        Spacer()
                    }
                    .transition(.scale)
                }
                else {
                    chatScrollView()
                }
            }
        }
    }

    @ViewBuilder private func chatScrollView() -> some View {
        ScrollViewReader { scroll in
            ScrollView(.vertical) {
                LazyVStack(alignment: .leading) {
                    ForEach(viewModel.messages, id: \.id) { message in
                        chatBubbleForMessage(message)
                        .onAppear {
                            if message.id == viewModel.messages.first?.id {
                                viewModel.didScrollToLast()
                            }
                        }
                        .transition(
                            .move(
                                edge: message.from.isCurrent ? .trailing : .leading
                            )
                        )
                    }
                }
                .padding([.horizontal, .bottom], 8)
            }
            .safeAreaInset(edge: .top) {
                EmptyView()
                    .frame(width: 0, height: 5)
            }
            .safeAreaInset(edge: .bottom) {
                EmptyView()
                    .frame(width: 0, height: 5)
            }
            .onAppear {
                if let last = viewModel.messages.last, viewModel.shouldScrollToFirst {
                    scroll.scrollTo(last.id, anchor: UnitPoint(x: 0.5, y: 1.1))
                }
            }
            .onChange(
                of: viewModel.messages
            ) { messages in
                if messages.count < 1 || viewModel.shouldScrollToFirst == false { return }
                withAnimation { scroll.scrollTo(messages.last!.id, anchor: .bottom) }
            }
        }
    }

    private func chatBubbleForMessage(_ message: Message) -> some View {
        ChatBubble(
            direction:
                message.from.isCurrent
            ? .right(drawTail: message.drawTail) : .left(drawTail: message.drawTail)
        ) {
            if let textMessage = message as? Message.TextMessage {
                VStack(alignment: message.from.isCurrent ? .trailing : .leading) {
                    if viewModel.drawForeignMessageMessageTitle && message.from.isCurrent == false {
                        HStack {
                            Text("From: \(message.from.name)")
                                .font(.system(size: 10))
                                .foregroundColor(Color(red: 245, green: 245, blue: 245))
                        }
                    }
                    Text(textMessage.content)
                        .foregroundColor(.white)
                    HStack {
                        Text(Date(timeIntervalSince1970: TimeInterval(message.sendDate)), format: Date.FormatStyle().hour().minute())
                            .font(.system(size: 10))
                        iconForMessage(message)
                            .resizable()
                            .frame(width: 10, height: 10)
                    }
                    .foregroundColor(Color(red: 245, green: 245, blue: 245))
                }
                .padding(.vertical, 10)
                .padding(.horizontal, 15)
                .background(message.from.isCurrent ? .green : .blue)
            } else {
                EmptyView()
            }
        }
    }

    private func iconForMessage(_ message: Message) -> Image {
        switch message.status {
        case .unknown: return Image(systemName: "checkmark.seal")
        case .received: return Image(systemName: "checkmark.seal")
        case .sending: return Image(systemName: "clock.arrow.circlepath")
        default: return Image(systemName: "clock.arrow.circlepath")
        }
    }

    public init(viewModel: ViewModel) {
        self.viewModel = viewModel
    }
}

#if DEBUG
struct ChatList_Previews: PreviewProvider {
    static var previews: some View {
        ChatList(viewModel: ChatListPreviewViewModel())
    }
}

private final class ChatListPreviewViewModel: ChatListViewModel {
    var shouldScrollToFirst: Bool = true

    func didScrollToFirst() {
        shouldScrollToFirst = false
    }

    var messages: [Message] = []

    var drawForeignMessageMessageTitle: Bool = false

    func didScrollToLast() {

    }
}
#endif
